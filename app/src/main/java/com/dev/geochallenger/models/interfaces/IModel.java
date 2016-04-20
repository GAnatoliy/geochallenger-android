package com.dev.geochallenger.models.interfaces;


import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;

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
}
