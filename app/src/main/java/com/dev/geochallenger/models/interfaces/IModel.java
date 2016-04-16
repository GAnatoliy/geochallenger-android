package com.dev.geochallenger.models.interfaces;


import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;

import java.util.List;


public interface IModel {
    void getPoiList(OnDataLoaded<List<Poi>> dataLoaded);

    void getPoiDetails(String id, OnDataLoaded<Poi> dataLoaded);

    void calculateRoute(String origin, String destination, String waypoints, String key, OnDataLoaded<GoogleDirectionsEntity> dataLoaded);
}
