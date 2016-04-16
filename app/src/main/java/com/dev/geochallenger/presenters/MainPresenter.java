package com.dev.geochallenger.presenters;

import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.IMainView;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class MainPresenter extends IPresenter<IMainView> {
    public MainPresenter(IMainView view) {
        super(view);
    }

    @Override
    public void init() {
        view.initMap();
    }
}
