package com.dev.geochallenger.models.interfaces;


import com.dev.geochallenger.models.entities.Poi;

import java.util.List;


public interface IModel {
    void init();

    void getPoiList(OnDataLoaded<List<Poi>> dataLoaded);

    void getPoiDetails(String id, OnDataLoaded<Poi> dataLoaded);

}
