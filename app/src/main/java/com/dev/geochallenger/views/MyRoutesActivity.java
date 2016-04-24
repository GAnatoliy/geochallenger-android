package com.dev.geochallenger.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.ExtraConstants;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.MyRoutesPresenter;
import com.dev.geochallenger.views.adapters.MyRoutesRecyclerAdapter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IMyRoutesView;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class MyRoutesActivity extends ABaseActivityView<MyRoutesPresenter> implements IMyRoutesView {
    private RecyclerView myRoutesRecyclerView;

    @Override
    protected MyRoutesPresenter createPresenter() {
        return new MyRoutesPresenter(this, RetrofitModel.getInstance(), new TokenRepository(getApplication()));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_route_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

        final ActionBar supportActionBar = getSupportActionBar();
        supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#111133")));
        supportActionBar.setDisplayHomeAsUpEnabled(true);

        myRoutesRecyclerView = (RecyclerView) findViewById(R.id.myRouteRecyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(MyRoutesActivity.this);
        myRoutesRecyclerView.setLayoutManager(layout);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public void updateRoutesList(List<RouteResponse> routeResponses) {
        MyRoutesRecyclerAdapter myRoutesRecyclerAdapter = new MyRoutesRecyclerAdapter(getApplicationContext(), routeResponses);
        myRoutesRecyclerView.setAdapter(myRoutesRecyclerAdapter);
        myRoutesRecyclerAdapter.setOnItemClickListener(new MyRoutesRecyclerAdapter.IOnItemClickListener() {
            @Override
            public void onClick(View view, int position, RouteResponse route) {
                presenter.routeClicked(route);
            }
        });
    }

    @Override
    public void openRoute(RouteResponse route) {
        Intent intent = new Intent(this, CreateRouteActivity.class);
        intent.putExtra(ExtraConstants.ROUTE, new Gson().toJson(route));
        startActivity(intent);
    }
}
