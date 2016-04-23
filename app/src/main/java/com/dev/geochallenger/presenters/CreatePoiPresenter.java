package com.dev.geochallenger.presenters;

import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.PoiRequest;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.ICreatePoiView;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by a_dibrivnyj on 4/23/16.
 */
public class CreatePoiPresenter extends IPresenter<ICreatePoiView> {
    private final IModel iModel;
    private final ITokenRepository iTokenRepository;

    public CreatePoiPresenter(ICreatePoiView view, IModel iModel, ITokenRepository iTokenRepository) {
        super(view);
        this.iModel = iModel;
        this.iTokenRepository = iTokenRepository;
    }

    @Override
    public void init() {

    }

    public void createPoi(PoiRequest poiRequest) {
        iModel.createPoi(poiRequest, iTokenRepository.getToken(), new OnDataLoaded<Poi>() {
            @Override
            public void onSuccess(Poi poi) {
                view.poiCreated(poi);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.hideProgress();
                if (error != null) {
                    try {
                        view.showErrorMessage("Error", error.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void updatePoi(PoiRequest poiRequest, long id) {
        iModel.updatePoi(poiRequest, iTokenRepository.getToken(), id, new OnDataLoaded<Poi>() {
            @Override
            public void onSuccess(Poi poi) {
                view.poiUpdated(poi);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.hideProgress();
                if (error != null) {
                    try {
                        view.showErrorMessage("Error", error.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
