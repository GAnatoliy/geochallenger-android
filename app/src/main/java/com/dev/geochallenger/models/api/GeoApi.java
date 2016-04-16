package com.dev.geochallenger.models.api;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.CitiesEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public interface GeoApi {

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("api/pois")
    Call<List<Poi>> listPois();

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("poi/{id}")
    Call<Poi> listPoiById(@Path("id") String _id);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @POST("poi/{id}")
    void createPoi(@Path("id") String _id);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("https://maps.googleapis.com/maps/api/directions/json")
    Call<GoogleDirectionsEntity> calculateRoute(@Query("origin") String origin,
                                                @Query("destination") String destination,
                                                @Query("waypoints") String waypoints,
                                                @Query("key") String key);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("https://maps.googleapis.com/maps/api/place/autocomplete/json")
    Call<CitiesEntity> getCities(@Query("input") String input,
                                 @Query("language") String language,
                                 @Query("key") String key);
}
