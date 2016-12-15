//package com.cborum.traverse;
//
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Paint;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
//import android.support.v4.app.Fragment;
//import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
//import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.target.BitmapImageViewTarget;
//import com.cborum.traverse.backend.*;
//import com.cborum.traverse.backend.User;
//import com.cborum.traverse.profile_views.Achievements;
//import com.cborum.traverse.profile_views.Details;
//import com.cborum.traverse.profile_views.ProfileViewManager;
//import com.cborum.traverse.utils.CredentialsManager;
//import com.cborum.traverse.utils.ImageUtils;
//
//import java.io.InputStream;
//import java.net.URL;
//import java.util.List;
//import java.util.concurrent.Callable;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executor;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.Future;
//import java.util.concurrent.RunnableFuture;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
//
//public class ProfileTab extends Fragment {
//
//    private final String TAG = "ProfileTab";
//    private ImageView profileImage;
//    private TextView username;
//    private RelativeLayout travelsTab;
//    private RelativeLayout achievementsTab;
//    private RelativeLayout detailsTab;
//    private List<RelativeLayout> tabs;
//    private TraverseApiWrapper wrapper;
//    private User user;
//
//
//    private ViewGroup viewContainer;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.profile_tab, container, false);
//
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//
//
//
//
//        initKeys();
//        setValues();
//
//        ProfileViewManager.activity = getActivity();
//        ProfileViewManager.context = getContext();
//        ProfileViewManager.fragment = this;
//        ProfileViewManager.inflater = getLayoutInflater(savedInstanceState);
//        ProfileViewManager.container = viewContainer;
//
////        ProfileViewManager.addView(Travels.getInstance(R.layout.profile_travels, travelsTab, R.id.travelsHeader));
//        ProfileViewManager.addView(Achievements.getInstance(R.layout.profile_achievements, achievementsTab, R.id.achievementsHeader));
//        ProfileViewManager.addView(Details.getInstance(R.layout.profile_details, detailsTab, R.id.detailsHeader));
//
//        ProfileViewManager.changeView(1);
//
//    }
//
//    public void initKeys() {
//        wrapper = TraverseApiWrapper.Companion.getInstance();
//        user = wrapper.getUser();
//        profileImage = (ImageView) getActivity().findViewById(R.id.profileImage);
//        username = (TextView) getActivity().findViewById(R.id.username);
////        travelsTab = (RelativeLayout) getActivity().findViewById(R.id.travelsTab);
//        achievementsTab = (RelativeLayout) getActivity().findViewById(R.id.achievementsTab);
//        detailsTab = (RelativeLayout) getActivity().findViewById(R.id.detailsTab);
//        viewContainer = (ViewGroup) getActivity().findViewById(R.id.viewContainer);
//    }
//
//
//    public void setValues() {
//        if(user!= null && user.getUsername() != null){
//            username.setText(user.getUsername());
//        }
//
////        String url = "http://www.gamelib.com.br/static/uploads/assets/junho-2014/carlton-banks.jpg";
////        Glide.with(getContext()).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(profileImage) {
////            @Override
////            protected void setResource(Bitmap resource) {
////                RoundedBitmapDrawable circularBitmapDrawable =
////                        RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
////                circularBitmapDrawable.setCircular(true);
////                profileImage.setImageDrawable(circularBitmapDrawable);
////            }
////        });
//    }
//
//    @Override
//    public void onDestroy() {
//        ProfileViewManager.inflater = null;
//        super.onDestroy();
//    }
//
//    private void logout() {
//        CredentialsManager.deleteCredentials(getContext());
//        startActivity(new Intent(getContext(), LoginActivity.class));
//        getActivity().finish();
//    }
//
//}
