package com.dev.geochallenger.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.dev.geochallenger.R;
import com.dev.geochallenger.models.RetrofitModel;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.presenters.MyRoutesPresenter;
import com.dev.geochallenger.views.adapters.MyRoutesRecyclerAdapter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IMyRoutesView;

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

        myRoutesRecyclerView = (RecyclerView) findViewById(R.id.myRouteRecyclerView);
        final LinearLayoutManager layout = new LinearLayoutManager(MyRoutesActivity.this);
        myRoutesRecyclerView.setLayoutManager(layout);

    }

    @Override
    public void updateRoutesList(List<RouteResponse> routeResponses) {
        MyRoutesRecyclerAdapter myRoutesRecyclerAdapter = new MyRoutesRecyclerAdapter(getApplicationContext(), routeResponses);
        myRoutesRecyclerView.setAdapter(myRoutesRecyclerAdapter);
    }
}
