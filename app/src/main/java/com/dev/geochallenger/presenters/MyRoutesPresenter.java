package com.dev.geochallenger.presenters;

import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IMyRoutesView;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class MyRoutesPresenter extends IPresenter<IMyRoutesView> {

    private final IModel model;
    private final ITokenRepository iTokenRepository;

    public MyRoutesPresenter(IMyRoutesView view, IModel model, ITokenRepository iTokenRepository) {
        super(view);
        this.model = model;
        this.iTokenRepository = iTokenRepository;
    }

    @Override
    public void init() {
        view.showProgress();
        model.getRoutesList(iTokenRepository.getToken(), new OnDataLoaded<List<RouteResponse>>() {
            @Override
            public void onSuccess(List<RouteResponse> routeResponses) {
                view.hideProgress();
                view.updateRoutesList(routeResponses);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                if (error != null) {
                    try {
                        view.showErrorMessage("Error", error.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                view.hideProgress();
            }
        });
    }
}
