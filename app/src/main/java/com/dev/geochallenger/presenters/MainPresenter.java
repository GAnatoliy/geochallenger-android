package com.dev.geochallenger.presenters;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.IMainView;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class MainPresenter extends IPresenter<IMainView> {
    private IModel iModel;

    public MainPresenter(IMainView view, IModel iModel) {
        super(view);
        this.iModel = iModel;
    }

    @Override
    public void init() {

        iModel.init();

        iModel.getPoiList(new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                view.initMap(pois);
            }

            @Override
            public void onError(Throwable t) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });


    }
}
