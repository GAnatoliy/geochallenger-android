package com.dev.geochallenger.views.interfaces;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public interface ICreateRouteView extends IView {

    void initMap();

    String getGoogleWebApiKey();

    void showRouteCalculationErrorMessage();

    int getRouteColor();

    void drawRouteInUiThread(PolylineOptions lineOptions, double distance);

    void hideProgressInUiThread();

    void populateAutocompeteList(boolean from, PlacesEntity placesEntity);
    
    void showPois(List<Poi> pois);

    void setOrigin(String origin);

    void setDestination(String destination);

    void showMyRoutes();

    void setSelectedPoisCount(int size);
}
