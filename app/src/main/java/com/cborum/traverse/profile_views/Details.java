package com.cborum.traverse.profile_views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.load.engine.Resource;
import com.cborum.traverse.LoginActivity;
import com.cborum.traverse.R;
import com.cborum.traverse.backend.TraverseApiWrapper;
import com.cborum.traverse.backend.TraverseApiWrapperKt;
import com.cborum.traverse.backend.User;
import com.cborum.traverse.utils.CredentialsManager;
import com.google.android.gms.vision.text.Text;
import com.squareup.okhttp.internal.Platform;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Kristian Nielsen on 10-11-2016.
 */

public class Details extends AbstractProfileView {

    protected static Details instance;
    private static RelativeLayout tab;
    private int layout;
    private int headerId;

    private User user;


    private TextView email;
    private TextView emailHeader;
    private TextView country;
    private TextView countryHeader;
    private TextView achievementPoints;
    private TextView achievementPointsHeader;
    private TextView id;
    private TextView idHeader;
    private TextView created;
    private TextView createdHeader;
    private Activity activity;


    private RelativeLayout logoutButton;

    protected Details(int layout, RelativeLayout tab, int headerId) {
        this.layout = layout;
        this.tab = tab;
        this.headerId = headerId;
    }

    public static Details getInstance(int layout, RelativeLayout tab, int headerId) {
        return (Details) getAbstractInstance(Details.class, layout, tab, headerId);
    }


    @Override
    public void onLoad() {

        TraverseApiWrapper wrapper = TraverseApiWrapper.Companion.getInstance();
        user = wrapper.getUser();
        activity = ProfileViewManager.activity;
        initKeys();
        setValues();

    }


    public void initKeys(){

        email = (TextView) activity.findViewById(R.id.email);
        emailHeader = (TextView) activity.findViewById(R.id.emailHeader);
        country = (TextView) activity.findViewById(R.id.country);
        countryHeader = (TextView) activity.findViewById(R.id.countryHeader);
        achievementPoints = (TextView) activity.findViewById(R.id.achievementPoints);
        achievementPointsHeader = (TextView) activity.findViewById(R.id.achievementPointsHeader);
        id = (TextView) activity.findViewById(R.id.id);
        idHeader = (TextView) activity.findViewById(R.id.idHeader);
        created = (TextView) activity.findViewById(R.id.created);
        createdHeader = (TextView) activity.findViewById(R.id.createdHeader);

        logoutButton = (RelativeLayout) activity.findViewById(R.id.logoutButton);
    }

    public void setValues(){
        int colorHeader = activity.getResources().getColor(R.color.details_header_active);
        int colorContent = activity.getResources().getColor(R.color.details_active);


        if (user != null) {
            if (user.getEmail() != null) {
                email.setText(user.getEmail());
                email.setTextColor(colorContent);
                emailHeader.setTextColor(colorHeader);
            }
            if (user.getCountry() != null) {
                country.setText(user.getCountry());
                country.setTextColor(colorContent);
                countryHeader.setTextColor(colorHeader);
            }
            if(user.getAchievement_points() >= 0){
                achievementPoints.setText(String.valueOf(user.getAchievement_points()));
                achievementPoints.setTextColor(colorContent);
                achievementPointsHeader.setTextColor(colorHeader);
            }
            if(user.get_id() != null){
                id.setText(getShortId(user.get_id()));
                id.setTextColor(colorContent);
                idHeader.setTextColor(colorHeader);
            }
            if(user.getCreated_at() != null){

                created.setText(getShortDate(user.getCreated_at()));
                created.setTextColor(colorContent);
                createdHeader.setTextColor(colorHeader);
            }

        }
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public String getShortId(String id){

        if(id.length() >= 10){
            return id.substring(0,7) + "...";
        }
        return id;
    }

    public String getShortDate(Date date){

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");

        return format.format(date);
    }

    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public void onDestroy() {

    }

    private Context getContext(){
        return ProfileViewManager.context;
    }

    private void logout() {
        CredentialsManager.deleteCredentials(getContext());
        ProfileViewManager.fragment.startActivity(new Intent(getContext(), LoginActivity.class));
        activity.finish();
    }





    @Override
    public int getLayout() {
        return layout;
    }

    @Override
    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    public RelativeLayout getTab() {
        return tab;
    }

    @Override
    public void setTab(RelativeLayout tab) {
        this.tab = tab;
    }

    @Override
    public int getHeaderId() {
        return headerId;
    }

    @Override
    public void setHeaderId(int headerId) {
        this.headerId = headerId;
    }
}
