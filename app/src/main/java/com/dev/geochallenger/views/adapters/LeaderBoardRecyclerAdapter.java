package com.dev.geochallenger.views.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.entities.LeaderBoardItem;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.presenters.LeaderboardPresenter;
import com.dev.geochallenger.views.adapters.holders.LeaderboardRecyclerItemHolder;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public class LeaderBoardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerItemHolder> {

    private List<LeaderBoardItem> items;
    private UserResponce currentUser;

    public LeaderBoardRecyclerAdapter(List<LeaderBoardItem> items) {
        this.items = items;
    }

    @Override
    public LeaderboardRecyclerItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.leaderboard_recycler_item_layout, parent, false);
        return new LeaderboardRecyclerItemHolder(v);
    }

    @Override
    public void onBindViewHolder(LeaderboardRecyclerItemHolder holder, int position) {
        final LeaderBoardItem leaderBoardItem = items.get(position);

        int color;
        if (currentUser != null) {
            if (currentUser.getEmail().equals(leaderBoardItem.getEmail())) {
                color = Color.parseColor("#ff9911");
            } else {
                color = Color.BLACK;
            }
        } else {
            color = Color.BLACK;
        }

        holder.title.setTextColor(color);
        holder.number.setTextColor(color);
        holder.points.setTextColor(color);
        holder.point_label.setTextColor(color);

        holder.title.setText(leaderBoardItem.getName());
        holder.number.setText(String.valueOf(position + 1));
        holder.points.setText(String.valueOf(leaderBoardItem.getPoints()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setNewItems(List<LeaderBoardItem> newItems) {
        items.clear();
        items.addAll(newItems);
    }

    public void setCurrentUser(UserResponce currentUser) {
        this.currentUser = currentUser;
    }

}
