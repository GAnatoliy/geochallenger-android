package com.dev.geochallenger.presenters;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.Access_points;
import com.dev.geochallenger.models.entities.cities.detailed.Location;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
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
    private IModel iModel;

    public MainPresenter(IMainView view, IModel iModel) {
        super(view);
        this.iModel = iModel;
    }

    @Override
    public void init() {

        iModel.getPoiList(new OnDataLoaded<List<Poi>>() {
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
        iModel.getPlaces(newText, key, new OnDataLoaded<PlacesEntity>() {
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
        iModel.getPlace(placeId, key, new OnDataLoaded<PlaceDetailedEntity>() {
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


}
