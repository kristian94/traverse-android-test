package com.cborum.traverse;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.cborum.traverse.backend.CheckinRequest;
import com.cborum.traverse.backend.Photo;
import com.cborum.traverse.backend.Place;
import com.cborum.traverse.backend.TraverseApiWrapper;
import com.cborum.traverse.utils.CredentialsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by ChristopherBorum on 09/10/16.
 */
public class DetailActivity extends Activity {
    private final String TAG = getClass().getSimpleName();
    public static final String EXTRA_PARAM_ID = "place_id";
    public static final String EXTRA_PARAM_IMAGE_ID = "image_id";
    public static final String NAV_BAR_VIEW_NAME = Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME;

    private ImageView mImageView;
    private TextView mPlaceName;
    private TextView mPlaceInfo;
    private TextView mImageAttributions;
    private Button mButton;
    private Activity activity;

    private TraverseApiWrapper taw;
    private Place place;
    private Location placeLocation;



    private String url;

    private boolean inRange;
    private boolean visited = false;

    // todo popups instead of toasts

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.place_card_detail);
        EventBus.getDefault().register(this);

        mImageView = (ImageView) findViewById(R.id.placeImage);
        mPlaceName = (TextView) findViewById(R.id.placeNameTextView);
        mPlaceInfo = (TextView) findViewById(R.id.detailInfo);
        mImageAttributions = (TextView) findViewById(R.id.imageAttribution);
        mButton = (Button) findViewById(R.id.imHereButton);
        mButton.setClickable(false);

        taw = TraverseApiWrapper.Companion.getInstance();
        place = taw.getPlaces().get(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));

        placeLocation = new Location("");
        placeLocation.setLatitude(place.getGeometry().getLocation().getLat());
        placeLocation.setLongitude(place.getGeometry().getLocation().getLng());
        mButton.setText(getFormattedDistanceTo(placeLocation));

        mButton.setOnClickListener(mOnClickListener);
        activity = this;


        if (place != null && place.getName() != null) {
            mPlaceName.setText(place.getName());
        }

        if (getIntent().getByteArrayExtra(EXTRA_PARAM_IMAGE_ID) != null) {
//            byte array af billede-thumbnail der bliver passet in til intentet
            byte[] bytes = getIntent().getByteArrayExtra(EXTRA_PARAM_IMAGE_ID);
            Glide.with(this).load(bytes).centerCrop().crossFade().into(mImageView);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            int color = Palette.from(bitmap).generate().getMutedColor(this.getResources().getColor(R.color.gray_lightest));
            this.findViewById(R.id.placeNameHolder).setBackgroundColor(color);
        } else {
            if (place.getPhotos() != null && place.getPhotos().size() > 0 && place.getPhotos().get(0).getPhoto_reference() != "") {
                Photo photo = place.getPhotos().get(0);
                String photoUrl = taw.getPhotoUrl(photo.getPhoto_reference());
                url = photoUrl;

//            Glide.with(this).load("https://i.ytimg.com/vi/d5ZOpQ5o2Ns/mqdefault.jpg").asBitmap().into(new SimpleTarget<Bitmap>() {
//                @Override
//                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
//                    System.out.println("it works?");
//                }
//            });

            Glide.with(this).load(photoUrl).placeholder(R.drawable.image_not_found).centerCrop().into(mImageView);
                // todo get html attributions - not important atm
                // mImageAttributions.setText(Html.fromHtml(photo.));
            }
        }


        mPlaceInfo.setText(place.getVicinity());
    }

    private void toastMessage(final String msg) {
        DetailActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Subscribe
    public void onLocationEvent(Location mLocation) {
        Log.d(TAG, "onLocationChanged called");
        if (placeLocation.distanceTo(mLocation) < 100) {
            mButton.setClickable(true);
            mButton.setAlpha(1);
            inRange = true;
        } else {
            mButton.setText(getFormattedDistanceTo(placeLocation));
            mButton.setAlpha(0.5f);
            mButton.setClickable(false);
            inRange = false;
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inRange && !visited) {
                new Thread(mRun).start();
            } else if (visited) {
                toastMessage("You have already been here");
            } else {
                toastMessage("You need to be closer");
            }
        }
    };

    private Runnable mRun = new Runnable() {
        @Override
        public void run() {
            boolean success = taw.checkin(new CheckinRequest(place.getPlace_id(),
                            place.getName(),
                            place.getGeometry().getLocation().getLat(),
                            place.getGeometry().getLocation().getLng()),
                    CredentialsManager.getCredentials(getApplicationContext()).getIdToken());
            Log.d(TAG, "checkin success = " + success);
            if (success) {
                mButton.setClickable(false);
                visited = true;
                toastMessage("Success! You now have " + taw.getUser().getAchievement_points() + " AP ;)");
            } else {
                //todo handle error
            }
        }
    };

    private String getFormattedDistanceTo(Location location) {
        float distance = taw.getLastKnownLocation().distanceTo(location);
        if (distance < 500) {
            return String.format(Locale.ENGLISH, "%.0f meters away", distance);
        } else {
            return String.format(Locale.ENGLISH, "%.2f km away", distance / 1000);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}