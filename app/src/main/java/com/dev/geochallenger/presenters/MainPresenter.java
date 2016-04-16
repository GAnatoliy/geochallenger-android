package com.dev.geochallenger.presenters;

import android.util.Log;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.CitiesEntity;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IMainView;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.io.IOException;
import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class MainPresenter extends IPresenter<IMainView> {
    private IModel iModel;


    private GoogleApiClient mGoogleApiClient;


    public MainPresenter(IMainView view, IModel iModel) {
        super(view);
        this.iModel = iModel;
    }

    @Override
    public void init() {

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

    public void findPlaces(String newText, String key) {
        iModel.getCities(newText, key, new OnDataLoaded<CitiesEntity>() {
            @Override
            public void onSuccess(CitiesEntity citiesEntity) {
                view.populateAutocompeteList(citiesEntity);
            }

            @Override
            public void onError(Throwable t) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

}
