package com.cborum.traverse;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;

// https://www.raywenderlich.com/103367/material-design

// geo fix 12.568637 55.675578 cph
// geo fix -73.988442 40.748858 ny
// geo fix 139.731926 35.708996 jp
// geo fix 12.575260 55.681424  runde..

public class MainActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();

    private final int REQUEST_PERMISSION_GET_PLACES = 1337;
    private final int REQUEST_PERMISSION_START_LOCATION_UPDATES = 1338;

    private int[] tabIcons = {
            R.drawable.ic_map_black_24dp,
            R.drawable.ic_my_location_black_24dp,
            R.drawable.ic_account_circle_black_24dp
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpTabs();
    }

    private void setUpTabs() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.top_bar_item_selected_color));
        for (int id : tabIcons) {
            TabLayout.Tab current = tabLayout.newTab().setIcon(id);
            if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                current.getIcon().setTint(this.getResources().getColor(R.color.top_bar_icon_color));
            }
            tabLayout.addTab(current);

        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

        Window window = this.getWindow();

        // clear FLAG_TRANSLUCENT_STATUS flag:

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);


        // finally change the color
//        window.setStatusBarColor(this.getResources().getColor(R.color.status_bar_color));
//        window.setNavigationBarColor(this.getResources().getColor(R.color.navigation_bar_color));
    }
}