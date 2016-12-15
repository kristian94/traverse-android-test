package com.cborum.traverse.profile_views;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cborum.traverse.ProfileTab;
import com.cborum.traverse.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kristian Nielsen on 12-11-2016.
 */

public class ProfileViewManager {

    private static List<AbstractProfileView> views = new ArrayList();
    public static ViewGroup container;
    public static Activity activity;
    public static ProfileTab fragment;
    public static Context context;
    public static LayoutInflater inflater;



    public static void changeView(int id){
        emptyContainer();
        loadView(views.get(id));
    }

    private static void loadView(AbstractProfileView view) {
        inflater.inflate(view.getLayout(), container);
        view.getTab().setBackgroundColor(activity.getResources().getColor(R.color.profile_tab_tab_highlight));
//        ((ImageView) view.getTab().getChildAt(0)).setImageTintList(activity.getColorStateList(R.color.profile_tab_tab_icon));
        ((ImageView) view.getTab().getChildAt(0)).setColorFilter(activity.getResources().getColor(R.color.profile_tab_tab_icon));
        TextView header = (TextView) activity.findViewById(view.getHeaderId());
        header.setTextColor(activity.getResources().getColor(R.color.profile_tab_text_highlight));
        view.onLoad();
    }

    private static void emptyContainer() {
        for(AbstractProfileView view: views){
            view.getTab().setBackgroundColor(activity.getResources().getColor(R.color.profile_tab_tab_shadow));
//            ((ImageView) view.getTab().getChildAt(0)).setImageTintList(activity.getColorStateList(R.color.profile_tab_tab_icon_shadow));
            ((ImageView) view.getTab().getChildAt(0)).setColorFilter(activity.getResources().getColor(R.color.profile_tab_tab_icon_shadow));
//            view.getTab().setBackground(activity.getDrawable(R.drawable.profile_tab_gradient));
            TextView header = (TextView) activity.findViewById(view.getHeaderId());
            header.setTextColor(activity.getResources().getColor(R.color.profile_tab_text_shadow));
        }
        container.removeAllViews();
    }

    public static void addView(AbstractProfileView view){
        views.add(view);
        setupOnClickEvents();
    }



    private static void setupOnClickEvents(){
        System.out.println("Setting up onclick events");
        for(AbstractProfileView view: views){
            RelativeLayout tab = view.getTab();
            if(!tab.hasOnClickListeners()){
                tab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tabOnClick(v);
                    }
                });
            }
        }
    }

    private static void tabOnClick(View v){
        RelativeLayout tab = (RelativeLayout) v;

        AbstractProfileView view = findViewByTab(tab);

        emptyContainer();
        loadView(view);
    }

    private static AbstractProfileView findViewByTab(RelativeLayout tab){
        for(AbstractProfileView view: views){
            if(view.getTab().equals(tab)){
                return view;
            }
        }
        return null;
    }



}
