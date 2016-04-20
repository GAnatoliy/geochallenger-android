package com.dev.geochallenger.models.interfaces;


import android.location.Location;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;

import java.util.List;

import retrofit2.http.Query;


public interface IModel {
    void getPoiList(OnDataLoaded<List<Poi>> dataLoaded);

    void getPoiList(Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude, OnDataLoaded<List<Poi>> callback);

    void getPoiDetails(String id, OnDataLoaded<Poi> dataLoaded);

    void calculateRoute(String origin, String destination, String waypoints, String key, OnDataLoaded<GoogleDirectionsEntity> dataLoaded);

    void getPlaces(String input, String key, final OnDataLoaded<PlacesEntity> dataLoaded);

    void getPlace(String placeid, String key, final OnDataLoaded<PlaceDetailedEntity> dataLoaded);

    void querySearch(String query, String key, OnDataLoaded<PlaceDetailedEntity> error);
}
