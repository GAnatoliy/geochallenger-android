package com.dev.geochallenger.models;

import com.dev.geochallenger.models.api.GeoApi;
import com.dev.geochallenger.models.entities.DefaultResponse;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.entities.login.LoginResponce;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.entities.routes.Route;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
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
    public void getPoiList(final OnDataLoaded<List<Poi>> callback) {
        final Call<List<Poi>> listCall = service.listPois("", null, null, null, null);
        listCall.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getPoiList(String query, Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude, final OnDataLoaded<List<Poi>> callback) {
        final Call<List<Poi>> listCall = service.listPois(query, topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude);
        listCall.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getPoiList(String query, final OnDataLoaded<List<Poi>> callback) {
        final Call<List<Poi>> listCall = service.listPois(query);
        listCall.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }


    @Override
    public void getPoiList(Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude, final OnDataLoaded<List<Poi>> callback) {
        final Call<List<Poi>> listCall = service.listPois(null, topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude);
        listCall.enqueue(new Callback<List<Poi>>() {
            @Override
            public void onResponse(Call<List<Poi>> call, Response<List<Poi>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<Poi>> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getPoiDetails(String id, final OnDataLoaded<Poi> callback) {
        final Call<Poi> poiCall = service.listPoiById(id);
        poiCall.enqueue(new Callback<Poi>() {
            @Override
            public void onResponse(Call<Poi> call, Response<Poi> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<Poi> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void calculateRoute(String origin, String destination, String waypoints, String key, final OnDataLoaded<GoogleDirectionsEntity> callback) {
        final Call<GoogleDirectionsEntity> poiCall = service.calculateRoute(origin, destination, waypoints, key);
        poiCall.enqueue(new Callback<GoogleDirectionsEntity>() {
            @Override
            public void onResponse(Call<GoogleDirectionsEntity> call, Response<GoogleDirectionsEntity> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<GoogleDirectionsEntity> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getPlaces(String input, String key, final OnDataLoaded<PlacesEntity> callback) {
        final Call<PlacesEntity> cities = service.getPlaces(input, "en", key);
        cities.enqueue(new Callback<PlacesEntity>() {
            @Override
            public void onResponse(Call<PlacesEntity> call, Response<PlacesEntity> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PlacesEntity> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getPlace(String placeid, String key, final OnDataLoaded<PlaceDetailedEntity> callback) {
        final Call<PlaceDetailedEntity> cities = service.getPlaceDetailed(placeid, key);
        cities.enqueue(new Callback<PlaceDetailedEntity>() {
            @Override
            public void onResponse(Call<PlaceDetailedEntity> call, Response<PlaceDetailedEntity> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<PlaceDetailedEntity> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void createRoute(Route route, String token, final OnDataLoaded<DefaultResponse> callback) {
        final Call<DefaultResponse> response = service.createRoute("bearer " + token, route);
        response.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void updateRoute(int routeId, Route route, String token, final OnDataLoaded<DefaultResponse> callback) {
        final Call<DefaultResponse> response = service.updateRoute(routeId, "bearer " + token, route);
        response.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void deleteRoute(int routeId, String token, final OnDataLoaded<DefaultResponse> callback) {
        final Call<DefaultResponse> response = service.deleteRoute(routeId, "bearer " + token);
        response.enqueue(new Callback<DefaultResponse>() {
            @Override
            public void onResponse(Call<DefaultResponse> call, Response<DefaultResponse> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<DefaultResponse> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getRoutesList(String token, final OnDataLoaded<List<RouteResponse>> callback) {
        final Call<List<RouteResponse>> response = service.getRoutesList("bearer " + token);
        response.enqueue(new Callback<List<RouteResponse>>() {
            @Override
            public void onResponse(Call<List<RouteResponse>> call, Response<List<RouteResponse>> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<RouteResponse>> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void login(String uid, String token, final OnDataLoaded<LoginResponce> callback) {
        final Call<LoginResponce> response = service.login("password", uid, token);
        response.enqueue(new Callback<LoginResponce>() {
            @Override
            public void onResponse(Call<LoginResponce> call, Response<LoginResponce> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<LoginResponce> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }

    @Override
    public void getUser(String token, final OnDataLoaded<UserResponce> callback) {
        final Call<UserResponce> response = service.getUser("bearer " + token);
        response.enqueue(new Callback<UserResponce>() {
            @Override
            public void onResponse(Call<UserResponce> call, Response<UserResponce> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError(new Throwable("error"), response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<UserResponce> call, Throwable t) {
                callback.onError(t, null);
            }
        });
    }
}
