package com.dev.geochallenger.presenters;

import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.DefaultResponse;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IChallengeView;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by Yuriy Diachenko on 24.04.2016.
 */
public class ChallengePresenter extends IPresenter<IChallengeView> {

    private IModel restClient;
    private Poi poi;
    private ITokenRepository tokenRepository;

    public ChallengePresenter(IChallengeView view, IModel restClient, Poi poi, ITokenRepository tokenRepository) {
        super(view);
        this.restClient = restClient;
        this.poi = poi;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void init() {

    }

    public void checkin() {
        view.showProgress();
        restClient.checkinPoi(poi.getId(), tokenRepository.getToken(), new Object(), new OnDataLoaded<DefaultResponse>() {
            @Override
            public void onSuccess(DefaultResponse defaultResponse) {
                view.hideProgress();
                view.checkingSuccess();
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
