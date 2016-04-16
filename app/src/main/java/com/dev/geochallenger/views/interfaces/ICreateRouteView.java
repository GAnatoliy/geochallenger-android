package com.dev.geochallenger.views.interfaces;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public interface ICreateRouteView extends IView {

    void initMap();

    String getGoogleWebApiKey();

    void showRouteCalculationErrorMessage();

    int getRouteColor();

    void drawRouteInUiThread(PolylineOptions lineOptions);

    void hideProgressInUiThread();
}
