package com.dev.geochallenger.models.api;

import com.dev.geochallenger.models.entities.login.LoginResponce;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.entities.routes.Route;
import com.dev.geochallenger.models.entities.DefaultResponse;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.entities.routes.RouteResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    Call<List<Poi>> listPois(@Query("query") String query,
                             @Query("topLeftLatitude") Double topLeftLatitude,
                             @Query("topLeftLongitude") Double topLeftLongitude,
                             @Query("bottomRightLatitude") Double bottomRightLatitude,
                             @Query("bottomRightLongitude") Double bottomRightLongitude);

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
    Call<PlacesEntity> getPlaces(@Query("input") String input,
                                 @Query("language") String language,
                                 @Query("key") String key);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("https://maps.googleapis.com/maps/api/place/details/json")
    Call<PlaceDetailedEntity> getPlaceDetailed(@Query("placeid") String placeid,
                                               @Query("key") String key);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("api/pois")
    Call<List<Poi>> listPois(@Query("query") String query);

    @POST("api/routes")
    Call<DefaultResponse> createRoute(@Header("authorization") String bearerToken,
                                      @Body Route route);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @PUT("api/routes/{routeId}")
    Call<DefaultResponse> updateRoute(@Path("routeId") int routeId,
                                      @Header("authorization") String bearerToken,
                                      @Body Route route);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @DELETE("api/routes/{routeId}")
    Call<DefaultResponse> deleteRoute(@Path("routeId") int routeId,
                                      @Header("authorization") String bearerToken);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("api/routes")
    Call<List<RouteResponse>> getRoutesList(@Header("authorization") String bearerToken);

    @Headers({
            "Accept: application/json",
            "Content-Type: application/x-www-form-urlencoded"})
    @FormUrlEncoded
    @POST("/token")
    Call<LoginResponce> login(@Field("grant_type") String grant_type, @Field("username") String uid, @Field("password") String token);


    @Headers({
            "Accept: application/json",
            "Content-Type: application/json"})
    @GET("api/users/me")
    Call<UserResponce> getUser(@Header("authorization") String bearerToken);


}
