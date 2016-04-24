package com.dev.geochallenger.views.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dev.geochallenger.R;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public class LeaderboardRecyclerItemHolder extends RecyclerView.ViewHolder {

    public TextView title;
    public TextView number;
    public TextView points;
    public TextView point_label;

    public LeaderboardRecyclerItemHolder(View itemView) {
        super(itemView);
        title = (TextView) itemView.findViewById(R.id.tv_leaderboard_name);
        number = (TextView) itemView.findViewById(R.id.tv_leaderboard_number);
        points = (TextView) itemView.findViewById(R.id.tv_leaderboard_point);
        point_label = (TextView) itemView.findViewById(R.id.tv_leaderboard_point_label);
    }
}
