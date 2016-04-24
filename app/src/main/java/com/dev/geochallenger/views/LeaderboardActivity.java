package com.dev.geochallenger.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.LeaderBoardItem;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.LeaderboardPresenter;
import com.dev.geochallenger.views.adapters.LeaderBoardRecyclerAdapter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.ILeaderboardView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public class LeaderboardActivity extends ABaseActivityView<LeaderboardPresenter> implements ILeaderboardView {

    LeaderBoardRecyclerAdapter leaderBoardRecyclerAdapter;
    private List<LeaderBoardItem> items;

    @Override
    protected LeaderboardPresenter createPresenter() {
        return new LeaderboardPresenter(this, RetrofitModel.getInstance(), new TokenRepository(getApplicationContext()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.leaderboard_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#111133")));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.leaderboard_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(LeaderboardActivity.this));
        leaderBoardRecyclerAdapter = new LeaderBoardRecyclerAdapter(new ArrayList<LeaderBoardItem>());
        recyclerView.setAdapter(leaderBoardRecyclerAdapter);
    }

    @Override
    public void updateLeaderBoardView(List<LeaderBoardItem> items) {
        Collections.sort(items, new Comparator<LeaderBoardItem>() {
            @Override
            public int compare(LeaderBoardItem lhs, LeaderBoardItem rhs) {
                return compareInts(rhs.getPoints(), lhs.getPoints());
            }
        });

        this.items = items;

        leaderBoardRecyclerAdapter.setNewItems(items);
        leaderBoardRecyclerAdapter.notifyDataSetChanged();
    }

    public static int compareInts(int lhs, int rhs) {
        return lhs < rhs ? -1 : (lhs == rhs ? 0 : 1);
    }

    @Override
    public void updateUserData(UserResponce loginResponce) {
        leaderBoardRecyclerAdapter.setCurrentUser(loginResponce);
        leaderBoardRecyclerAdapter.notifyDataSetChanged();

        final View currentUser = findViewById(R.id.current_user);
        currentUser.setVisibility(View.VISIBLE);

        TextView tvLeaderboardName = (TextView) findViewById(R.id.tv_leaderboard_name);
        TextView tvLeaderboardRank = (TextView) findViewById(R.id.tv_leaderboard_rank);
        TextView tvLeaderboardPoint = (TextView) findViewById(R.id.tv_leaderboard_point);

        LeaderBoardItem leaderBoardItem = null;
        for (LeaderBoardItem item : items) {
            if (item.getEmail().equals(loginResponce.getEmail())) {
                leaderBoardItem = item;
                break;
            }
        }
        if (leaderBoardItem != null) {
            tvLeaderboardName.setText(leaderBoardItem.getName());
            tvLeaderboardRank.setText(String.valueOf(items.indexOf(leaderBoardItem) + 1));
            tvLeaderboardPoint.setText(String.valueOf(leaderBoardItem.getPoints()));
        }

    }
}
