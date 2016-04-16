package com.dev.geochallenger.models;

import com.dev.geochallenger.models.api.GeoApi;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */

public class RetrofitModel implements IModel {

    private static final String API_PATH = "http://testing-geochallenger-api.azurewebsites.net/";

    private GeoApi service;

    @Override
    public void init() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_PATH)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GeoApi.class);
    }

    @Override
    public void getPoiList(final OnDataLoaded<List<Poi>> dataLoaded) {
        final Call<List<Poi>> listCall = service.listPois();
        listCall.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                dataLoaded.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                dataLoaded.onError(t);
            }
        });
    }

    @Override
    public void getPoiDetails(String id, final OnDataLoaded<Poi> dataLoaded) {
        final Call<Poi> poiCall = service.listPoiById(id);
        poiCall.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                dataLoaded.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                dataLoaded.onError(t);
            }
        });
    }


}
