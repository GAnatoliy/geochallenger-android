package com.dev.geochallenger.presenters;

import android.location.Address;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.login.LoginResponce;
import com.dev.geochallenger.models.entities.login.UserResponce;
import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.repositories.TokenRepository;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.IMainView;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class MainPresenter extends IPresenter<IMainView> {
    private IModel model;
    private IGeocoder geocoder;
    private final ITokenRepository iTokenRepository;
    private boolean isStopped;
    private LatLng selectedPlaceLocation;
    private Address selectedPlaceAddress;
    private android.location.Location myLocation;

    public MainPresenter(IMainView view, IModel iModel, IGeocoder geocoder, ITokenRepository iTokenRepository) {
        super(view);
        this.model = iModel;
        this.geocoder = geocoder;
        this.iTokenRepository = iTokenRepository;
    }

    @Override
    public void init() {
        initAllPois();

        if (!TextUtils.isEmpty(iTokenRepository.getToken())) {
            getUser(iTokenRepository.getToken());
        }
    }

    public void initAllPois() {
        model.getPoiList(new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                view.initMap(pois);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
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
            public void onError(Throwable t, ResponseBody responseBody) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

    public void getDetailedPlaceInfo(String placeId, String key) {
        model.getPlace(placeId, key, new OnDataLoaded<PlaceDetailedEntity>() {
            @Override
            public void onSuccess(PlaceDetailedEntity entity) {
                view.setMapLocation(entity);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }


    public void onLocationSelected(LatLng latLng, final boolean isPoiSelected) {
        selectedPlaceLocation = latLng;
        if (!isPoiSelected) {
            view.setCustomMarker(latLng);
            view.showPlaceDetails();
        }
        geocoder.getAddress(latLng, new IGeocoder.IGeocoderListener() {

            @Override
            public void onAddressFetched(Address address) {
                selectedPlaceAddress = address;
                if (!isPoiSelected) {
                    if (!isStopped) {
                        view.updatePlaceDetailsWithAddress(address);
                    }
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

    public void queryText(String query, Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude) {
        model.getPoiList(query, topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude, new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                view.initMap(pois);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.showErrorMessage("Error", t.getMessage());
                view.hideProgress();
            }
        });
    }

    public void queryText(String query) {
        model.getPoiList(query, new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                view.initMap(pois);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.showErrorMessage("Error", t.getMessage());
                view.hideProgress();
            }
        });
    }

    public void login(String email, final String token) {
        model.login(email, token, new OnDataLoaded<LoginResponce>() {
            @Override
            public void onSuccess(LoginResponce loginResponce) {
                view.updateUserToken(loginResponce);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.invalidToken(token);
                try {
                    view.showErrorMessage("Error", error.string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                view.hideProgress();
            }
        });
    }

    public void getUser(String token) {
        model.getUser(token, new OnDataLoaded<UserResponce>() {
            @Override
            public void onSuccess(UserResponce loginResponce) {
                view.updateUserData(loginResponce);
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.showErrorMessage("Error", t.getMessage());
                view.hideProgress();
            }
        });
    }


    public void getPoiDetails(Poi poi) {
        if (poi != null) {
            view.showProgress();
            model.getPoiDetails(String.valueOf(poi.getId()), new OnDataLoaded<Poi>() {
                @Override
                public void onSuccess(Poi poi) {
                    view.hideProgress();
                    view.setDetailedPoiInfo(poi);

                }

                @Override
                public void onError(Throwable t, @Nullable ResponseBody error) {
                    view.hideProgress();
                    if (error != null) {
                        try {
                            view.showErrorMessage("Error", error.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    public void createPoiClicked() {
        view.showCreatePoiScreen(selectedPlaceLocation);
    }

    public void updateSelectedLocation(Poi poi) {
        view.updateCreatedMarker(poi);
    }
}
