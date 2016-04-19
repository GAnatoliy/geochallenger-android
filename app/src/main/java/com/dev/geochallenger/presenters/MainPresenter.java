package com.dev.geochallenger.presenters;

import android.location.Address;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.Access_points;
import com.dev.geochallenger.models.entities.cities.detailed.Location;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IMainView;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class MainPresenter extends IPresenter<IMainView> {
    private IModel model;
    private IGeocoder geocoder;
    private boolean isStopped;
    private LatLng selectedPlaceLocation;
    private Address selectedPlaceAddress;
    private android.location.Location myLocation;

    public MainPresenter(IMainView view, IModel iModel, IGeocoder geocoder) {
        super(view);
        this.model = iModel;
        this.geocoder = geocoder;
    }

    @Override
    public void init() {

        model.getPoiList(new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                view.initMap(pois);
            }

            @Override
            public void onError(Throwable t) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

    public void findPlaces(String newText, String key) {
        model.getPlaces(newText, key, new OnDataLoaded<PlacesEntity>() {
            @Override
            public void onSuccess(PlacesEntity placesEntity) {
                view.populateAutocompeteList(placesEntity);
            }

            @Override
            public void onError(Throwable t) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

    public void getDetailedPlaceInfo(String placeId, String key) {
        model.getPlace(placeId, key, new OnDataLoaded<PlaceDetailedEntity>() {
            @Override
            public void onSuccess(PlaceDetailedEntity entity) {
                final Access_points access_points = entity.getResult().getGeometry().getAccess_points()[0];
                final Location location = access_points.getLocation();
                view.setMapLocation(new LatLng(Double.valueOf(location.getLat()), Double.valueOf(location.getLng())));
            }

            @Override
            public void onError(Throwable t) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }


    public void onMapLongClick(LatLng latLng) {
        selectedPlaceLocation = latLng;
        view.setCustomMarker(latLng);
        view.showPlaceDetails();
        geocoder.getAddress(latLng, new IGeocoder.IGeocoderListener() {

            @Override
            public void onAddressFetched(Address address) {
                selectedPlaceAddress = address;
                if (!isStopped) {
                    view.updatePlaceDetailsWithAddress(address);
                }
            }

            @Override
            public void onError(Exception e) {
                //ignore
            }
        });
    }

    public void onStop() {
        isStopped = true;
    }

    public void onStart() {
        isStopped = false;
    }

    public void createRouteClicked() {
        view.showCreateRouteScreen(selectedPlaceLocation, selectedPlaceAddress, myLocation);
    }

    public void setMyLocation(android.location.Location myLocation) {
        this.myLocation = myLocation;
    }
}
