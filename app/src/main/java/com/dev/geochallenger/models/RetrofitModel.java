package com.dev.geochallenger.models;

import com.dev.geochallenger.models.api.GeoApi;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.CitiesEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */

public class RetrofitModel implements IModel {

    private static RetrofitModel instance;

    private static final String API_PATH = "http://testing-geochallenger-api.azurewebsites.net/";

    private volatile static GeoApi service;

    private RetrofitModel() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_PATH)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(GeoApi.class);
    }

    public static RetrofitModel getInstance() {
        if (instance == null) {
            instance = new RetrofitModel();
        }
        return instance;
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

    @Override
    public void calculateRoute(String origin, String destination, String waypoints, String key, final OnDataLoaded<GoogleDirectionsEntity> dataLoaded) {
        final Call<GoogleDirectionsEntity> poiCall = service.calculateRoute(origin, destination, waypoints, key);
        poiCall.enqueue(new Callback<GoogleDirectionsEntity>() {
            @Override
            public void onResponse(Call<GoogleDirectionsEntity> call, Response<GoogleDirectionsEntity> response) {
                dataLoaded.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<GoogleDirectionsEntity> call, Throwable t) {
                dataLoaded.onError(t);
            }
        });
    }

    @Override
    public void getCities(String input, String key, final OnDataLoaded<CitiesEntity> dataLoaded) {
        final Call<CitiesEntity> cities = service.getCities(input, "en", key);
        cities.enqueue(new Callback<CitiesEntity>() {
            @Override
            public void onResponse(Call<CitiesEntity> call, Response<CitiesEntity> response) {
                dataLoaded.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<CitiesEntity> call, Throwable t) {
                dataLoaded.onError(t);
            }
        });
    }


}
