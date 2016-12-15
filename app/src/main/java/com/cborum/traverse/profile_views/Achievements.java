package com.cborum.traverse.profile_views;

import android.app.Activity;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cborum.traverse.AchievementAdapter;
import com.cborum.traverse.R;
import com.cborum.traverse.backend.Achievement;
import com.cborum.traverse.backend.TraverseApiWrapper;
import com.cborum.traverse.backend.User;

/**
 * Created by Kristian Nielsen on 10-11-2016.
 */

public class Achievements extends AbstractProfileView {

    protected static Achievements instance;
    private static RelativeLayout tab;
    private int layout;
    private int headerId;
    private ListView listView;




    protected Achievements(int layout, RelativeLayout tab, int headerId) {
        this.layout = layout;
        this.tab = tab;
        this.headerId = headerId;
    }

    public static Achievements getInstance(int layout, RelativeLayout tab, int headerId){
        return (Achievements) getAbstractInstance(Achievements.class, layout, tab, headerId);
    }

    @Override
    public void onLoad() {

        Activity activity = ProfileViewManager.activity;
        TraverseApiWrapper wrapper = TraverseApiWrapper.Companion.getInstance();
        User user = wrapper.getUser();

        listView = (ListView) activity.findViewById(R.id.achievementsContainer);

        AchievementAdapter adapter = new AchievementAdapter(
                ProfileViewManager.context,
                R.layout.profile_achievements_list_item,
                R.id.achievementTitle,
                wrapper.getUser().getAchievements());


        listView.setAdapter(adapter);

        for(Achievement ach: user.getAchievements()){
            System.out.println(ach.getTitle());
        }

    }

    @Override
    public void onDestroy() {

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
