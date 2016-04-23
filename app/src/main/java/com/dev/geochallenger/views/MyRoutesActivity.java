package com.dev.geochallenger.views;

import android.os.Bundle;

import com.dev.geochallenger.R;
import com.dev.geochallenger.presenters.MyRoutesPresenter;
import com.dev.geochallenger.views.interfaces.ABaseActivityView;
import com.dev.geochallenger.views.interfaces.IMyRoutesView;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class MyRoutesActivity extends ABaseActivityView<MyRoutesPresenter> implements IMyRoutesView {

    @Override
    protected MyRoutesPresenter createPresenter() {
        return new MyRoutesPresenter(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.my_route_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        super.onViewCreated(savedInstanceState);

    }
}
