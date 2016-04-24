package com.dev.geochallenger.models.interfaces;


import com.dev.geochallenger.models.entities.LeaderBoardItem;
import com.dev.geochallenger.models.entities.PoiRequest;
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


public interface IModel {
    void getPoiList(OnDataLoaded<List<Poi>> dataLoaded);

    void getPoiList(String query, Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude, OnDataLoaded<List<Poi>> callback);

    void getPoiList(String query, OnDataLoaded<List<Poi>> callback);

    void getPoiList(Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude, OnDataLoaded<List<Poi>> callback);

    void getPoiDetails(String id, OnDataLoaded<Poi> dataLoaded);

    void calculateRoute(String origin, String destination, String waypoints, String key, OnDataLoaded<GoogleDirectionsEntity> dataLoaded);

    void getPlaces(String input, String key, final OnDataLoaded<PlacesEntity> dataLoaded);

    void getPlace(String placeid, String key, final OnDataLoaded<PlaceDetailedEntity> dataLoaded);

    void createRoute(Route route, String token, OnDataLoaded<DefaultResponse> callback);

    void updateRoute(int routeId, Route route, String token, OnDataLoaded<DefaultResponse> callback);

    void deleteRoute(int routeId, String token, OnDataLoaded<DefaultResponse> callback);

    void getRoutesList(String token, OnDataLoaded<List<RouteResponse>> callback);

    void login(String uid, String token, OnDataLoaded<LoginResponce> callback);

    void getUser(String token, OnDataLoaded<UserResponce> callback);

    void createPoi(PoiRequest poiRequest, String token, final OnDataLoaded<Poi> callback);

    void updatePoi(PoiRequest poiRequest, String token, long id, final OnDataLoaded<Poi> callback);

    void checkinPoi(long poi, String token, Object object, OnDataLoaded<DefaultResponse> callback);

    void getLeaderBoard(OnDataLoaded<List<LeaderBoardItem>> callback);
}
