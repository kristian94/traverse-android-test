package com.cborum.traverse;

import android.Manifest;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.cborum.traverse.backend.TraverseApiWrapper;

import com.cborum.traverse.utils.CredentialsManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;

// todo rename to something else than PlacesGridTab

// https://developer.android.com/guide/components/fragments.html
// https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=55.675578,12.568637&radius=5000&name=attraction&rankby=prominence&key=AIzaSyAF5p8_MfW9EIM7WEey9EGxBt0fKcmqOOk
// todo alert: We do not condone trespassing.. eller noget
// todo move googleapiclient and location listener out of this file, and implement interface that can handle it

public class PlacesGridTab extends Fragment {
    private final String TAG = getClass().getSimpleName();

    private TraverseApiWrapper taw;

    private RecyclerView recyclerView;
    private CardAdapter adapter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.places_grid_tab, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getActivity().requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1337);

        getActivity().findViewById(R.id.noPlacesFound).setVisibility(View.INVISIBLE);

        taw = TraverseApiWrapper.Companion.getInstance();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recycler_view);
        adapter = new CardAdapter(getContext());
        progressBar = (ProgressBar) getActivity().findViewById(R.id.ctrlActivityIndicator);
        CardAdapter.OnItemClickListener onItemClickListener = new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
//                Toast.makeText(getContext(), "Clicked " + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getContext(), DetailActivity.class);
                intent.putExtra(DetailActivity.EXTRA_PARAM_ID, position);

//              Pass image to intent

                ImageView imageView = (ImageView) v.findViewById(R.id.thumbnail);
                if(imageView.getDrawable() != null){
                    Bitmap bitmap = ((GlideBitmapDrawable)imageView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bitmapdata = stream.toByteArray();

                    intent.putExtra(DetailActivity.EXTRA_PARAM_IMAGE_ID, bitmapdata);
                }

                startActivity(intent);
            }
        };
        adapter.setOnItemClickListener(onItemClickListener);



        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    private void getPlaces(final Location location) {
        // todo move this to locationlistenerwrapper (den må borum ligge og rode med)
        Log.d(TAG, "fetchPlaces called");
        if (location == null) {
            Log.d(TAG, "location is null, returned");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "fetching places");
                boolean newResult = taw.fetchPlaces(location.getLatitude(), location.getLongitude(), CredentialsManager.getCredentials(getContext()).getIdToken());

                updateDataSet();

            }
        }).start();
    }

    private void updateDataSet() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                ((CardAdapter) recyclerView.getAdapter()).notifyDataSetChanged();
                if(taw.getPlaces() == null || taw.getPlaces().size() == 0){
                    System.out.println("set visible");
                    getActivity().findViewById(R.id.noPlacesFound).setVisibility(View.VISIBLE);
                }else{
                    System.out.println("set invisible");
                    getActivity().findViewById(R.id.noPlacesFound).setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    @Subscribe
    public void onLocationEvent(Location mLocation) {
        Log.d(TAG, "onLocationChanged called");
        Log.d(TAG, String.format("Location changed to: %f,%f", mLocation.getLatitude(), mLocation.getLongitude()));
        if (taw.getLastKnownLocation() == null || taw.getLastKnownLocation().distanceTo(mLocation) > 10) { // (temp) if the new location is more than 10 meters away from old location fetch places
            if(taw.getPlacesSize() == 0){
                progressBar.setVisibility(View.VISIBLE);
            }
            taw.setLastKnownLocation(mLocation);
            getPlaces(mLocation);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        Log.d(TAG, "onStart");
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
        Log.d(TAG, "onStop");
    }

    /*@Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_GET_PLACES: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permision granted for location, can fetch places again...
                    startLocationUpdates();
                } else {
                    // todo
                    stopLocationUpdates();
                    // Disable the functionality that depends on this permission.
                }
                return;
            }
            case REQUEST_PERMISSION_START_LOCATION_UPDATES: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationUpdates();
                } else {
                    stopLocationUpdates();
                    // todo
                    // Disable the functionality that depends on this permission.
                }
                return;
            }
        }
    }*/

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
