package com.cborum.traverse;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cborum.traverse.backend.Achievement;

import java.util.List;

/**
 * Created by Kristian Nielsen on 04-12-2016.
 */

public class AchievementAdapter extends ArrayAdapter<Achievement> {



    public AchievementAdapter(Context context, int resource, int textViewResourceId, List<Achievement> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Achievement achievement = getItem(position);



        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.profile_achievements_list_item, parent, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.achievementTitle);
        TextView type = (TextView) convertView.findViewById(R.id.achievementType);

        title.setText(achievement.getTitle());

        type.setText(achievement.getAchievement_type());

        return convertView;

    }
}
