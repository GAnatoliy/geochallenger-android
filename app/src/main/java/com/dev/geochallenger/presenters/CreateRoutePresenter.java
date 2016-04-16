package com.dev.geochallenger.presenters;

import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class CreateRoutePresenter extends IPresenter<ICreateRouteView> {

    public CreateRoutePresenter(ICreateRouteView view) {
        super(view);
    }

    @Override
    public void init() {
        view.initMap();
    }

    public void getPathForCities(String source, String destination, IPathCallback callback) {

    }

    public interface IPathCallback {
        void onPathCalculated();
        void onError(Exception e);
    }
}
