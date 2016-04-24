package com.dev.geochallenger.views.interfaces;

import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.login.LoginResponce;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public interface IMainView extends IView {

    void initMap(List<Poi> pois);

    void populateAutocompeteList(PlacesEntity placesEntity);

    void setMapLocation(PlaceDetailedEntity latLng);

    void setCustomMarker(LatLng latLng);

    void showPlaceDetails();

    void updatePlaceDetailsWithAddress(Address address);

    void showCreateRouteScreen(LatLng selectedPlaceLocation, @Nullable Address selectedPlaceAddress, @Nullable Location myLocation);

    void updateUserToken(LoginResponce loginResponce);

    void updateUserData(UserResponce loginResponce);

    void invalidToken(String token);

    void setDetailedPoiInfo(Poi poi);

    void showCreatePoiScreen(LatLng selectedPlaceLocation);

    void updateCreatedMarker(Poi selectedPlaceLocation);
}

