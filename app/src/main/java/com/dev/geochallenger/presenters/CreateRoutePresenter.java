package com.dev.geochallenger.presenters;

import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.dev.geochallenger.models.entities.DefaultResponse;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.cities.detailed.PlaceDetailedEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.entities.directions.Leg;
import com.dev.geochallenger.models.entities.directions.Route;
import com.dev.geochallenger.models.entities.routes.RouteResponse;
import com.dev.geochallenger.models.interfaces.IGeocoder;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.parsers.DirectionsJSONParser;
import com.dev.geochallenger.models.repositories.interfaces.ITokenRepository;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class CreateRoutePresenter extends IPresenter<ICreateRouteView> {

    private IModel restClient;
    private String source;
    private String destination;
    private double routeDistance;
    private Location myLocation;
    private ITokenRepository tokenRepository;
    private RouteResponse routeToEdit;
    private IGeocoder geocoder;
    private LatLng selectedLocation;
    private Address selectedAddress;
    private String routePath = "";
    private List<Poi> poisNearMe;
    private List<Poi> waypoints = new ArrayList<>();

    public CreateRoutePresenter(ICreateRouteView view, IModel restClient, LatLng selectedLocation,
                                Address selectedAddress, Location myLocation, ITokenRepository tokenRepository,
                                RouteResponse routeResponse, IGeocoder geocoder) {
        super(view);
        this.restClient = restClient;
        this.selectedLocation = selectedLocation;
        this.selectedAddress = selectedAddress;
        this.myLocation = myLocation;
        this.tokenRepository = tokenRepository;
        routeToEdit = routeResponse;
        this.geocoder = geocoder;
    }

    @Override
    public void init() {
        view.initMap();
        if (!checkIfInEditingMode()) {
            if (selectedLocation != null && myLocation != null) {

                getPathForCities(myLocation.getLatitude() + "," + myLocation.getLongitude(), selectedLocation.latitude + "," + selectedLocation.longitude);
            }
            if (myLocation != null) {
                geocoder.getAddress(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), new IGeocoder.IGeocoderListener() {
                    @Override
                    public void onAddressFetched(Address address) {
                        if (address != null && address.getMaxAddressLineIndex() >= 0) {
                            String addressString = "";
                            int maxAddressLineIndex = address.getMaxAddressLineIndex();

                            addressString += maxAddressLineIndex >= 0 ? address.getAddressLine(0) : "";
                            if (!addressString.equals("")) {
                                addressString += maxAddressLineIndex >= 1 ? ", " + address.getAddressLine(1) : "";
                            } else {
                                addressString += maxAddressLineIndex >= 1 ? address.getAddressLine(1) : "";
                            }
                            view.setOrigin(addressString);
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        view.setOrigin("My location");
                    }
                });
            }
            if (selectedAddress != null && selectedAddress.getMaxAddressLineIndex() >= 0) {
                String address = "";
                int maxAddressLineIndex = selectedAddress.getMaxAddressLineIndex();

                address += maxAddressLineIndex >= 0 ? selectedAddress.getAddressLine(0) : "";
                if (!address.equals("")) {
                    address += maxAddressLineIndex >= 1 ? ", " + selectedAddress.getAddressLine(1) : "";
                } else {
                    address += maxAddressLineIndex >= 1 ? selectedAddress.getAddressLine(1) : "";
                }
                view.setDestination(address);
            } else if (selectedLocation != null) {
                view.setDestination(String.format("%.4f,%.4f", selectedLocation.latitude, selectedLocation.longitude));
            }
        }
    }

    private boolean checkIfInEditingMode() {
        if (routeToEdit != null) {

            if (!TextUtils.isEmpty(routeToEdit.getStartAddress())) {
                source = routeToEdit.getStartAddress();
            } else {
                source = String.format("%.4f,%.4f", routeToEdit.getStartPointLatitude(), routeToEdit.getStartPointLongitude());
            }

            if (!TextUtils.isEmpty(routeToEdit.getEndAddress())) {
                destination = routeToEdit.getEndAddress();
            } else {
                destination = String.format("%.4f,%.4f", routeToEdit.getEndPointLatitude(), routeToEdit.getEndPointLongitude());
            }

            routeDistance = routeToEdit.getDistanceInMeters();

            myLocation = new Location("");
            myLocation.setLatitude(routeToEdit.getStartPointLatitude());
            myLocation.setLongitude(routeToEdit.getStartPointLongitude());

            selectedLocation = new LatLng(routeToEdit.getEndPointLatitude(), routeToEdit.getEndPointLongitude());

            routePath = routeToEdit.getRoutePath();
            waypoints = routeToEdit.getPois();
            view.setOrigin(source);
            view.setDestination(destination);

            view.setSelectedPoisCount(waypoints.size());
            convertPathAndDraw();
            return true;

        }
        return false;
    }

    public void getPathForCities(String source, String destination) {
        this.source = source;
        this.destination = destination;
        view.showProgress();
        restClient.calculateRoute(source, destination, convertWaypoints(), view.getGoogleWebApiKey(), new OnDataLoaded<GoogleDirectionsEntity>() {
            @Override
            public void onSuccess(GoogleDirectionsEntity googleDirectionsEntity) {
                parseDirections(googleDirectionsEntity);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.hideProgress();
                view.showRouteCalculationErrorMessage();
            }
        });
    }

    private String convertWaypoints() {

        StringBuilder convertedString = new StringBuilder();
        if (waypoints.size() > 0) {
            //optimize waypoints order
            convertedString.append("optimize:true");
            convertedString.append("|");
        }
        for (int i = 0; i < waypoints.size(); i++) {
            Poi poi = waypoints.get(i);
            //convertedString.append("via:");
            convertedString.append(poi.getLatitude());
            convertedString.append(",");
            convertedString.append(poi.getLongitude());
            convertedString.append("|");
        }

        return convertedString.length() > 0 ? convertedString.substring(0, convertedString.length() - 1) : null;
    }

    public int toggleWaypoints(LatLng point) {
        return toggleWaypoints(getPoiByLocation(point));

    }

    public int toggleWaypoints(Poi poi) {
        if (poi != null) {
            if (waypoints.contains(poi)) {
                waypoints.remove(poi);

            } else {
                waypoints.add(poi);
            }
        }

        getPathForCities(source, destination);

        return waypoints.size();
    }

    public boolean isWaypoint(Poi poi) {
        return waypoints.contains(poi);
    }


    private Poi getPoiByLocation(LatLng location) {
        for (int i = 0; i < poisNearMe.size(); i++) {
            Poi poi = poisNearMe.get(i);
            if (poi.getLatitude() == location.latitude
                    && poi.getLongitude() == location.longitude) {
                return poi;
            }
        }
        return null;
    }

    private void parseDirections(final GoogleDirectionsEntity entity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    double distance = 0;
                    routePath = "";

                    List<Route> entityRoutes = entity.getRoutes();

                    /** Traversing all routes */
                    if (entityRoutes != null) {
                        for (int i = 0; i < entityRoutes.size(); i++) {
                            routePath += entityRoutes.get(i).getOverviewPolyline().getPoints();
                        }
                    }

                    List<Route> routeList = entity.getRoutes();
                    if (routeList != null) {
                        for (int i = 0; i < routeList.size(); i++) {
                            List<Leg> legList = routeList.get(i).getLegs();
                            if (legList != null) {
                                for (Leg leg : legList) {
                                    distance += leg.getDistance().getValue();
                                }
                            }
                        }
                    }

                    routeDistance = distance;

                    convertPathAndDraw();

                } catch (Exception e) {
                    e.printStackTrace();
                    view.hideProgressInUiThread();
                }
            }
        }).start();
    }

    private void convertPathAndDraw() {

        List<List<HashMap<String, String>>> routes = new DirectionsJSONParser().parse(routePath);

        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = new PolylineOptions();
        MarkerOptions markerOptions = new MarkerOptions();
        String duration = "";


        // Traversing through all the routes
        for (int i = 0; i < routes.size(); i++) {
            points = new ArrayList<LatLng>();

            // Fetching i-th route
            List<HashMap<String, String>> path = routes.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);
                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
        }

        view.drawRouteInUiThread(lineOptions, routeDistance * 0.001);

    }

    public void getPoisByViewPort(Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude) {
        restClient.getPoiList(topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude, new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                if (pois != null) {
                    for (int i = 0; i < pois.size(); i++) {
                        Poi poi = pois.get(i);
                        if (waypoints.contains(poi)) {
                            poi.setWaypoint(true);
                        }
                    }
                }
                poisNearMe = pois;
                view.showPois(pois);
                view.hideProgress();
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.hideProgress();
            }
        });
    }

    public void createRoute(String name) {
        view.showProgress();

        com.dev.geochallenger.models.entities.routes.Route route = new com.dev.geochallenger.models.entities.routes.Route();
        route.setDistanceInMeters(routeDistance);
        route.setEndPointLatitude(selectedLocation.latitude);
        route.setEndPointLongitude(selectedLocation.longitude);
        route.setStartPointLatitude(myLocation.getLatitude());
        route.setStartPointLongitude(myLocation.getLongitude());
        route.setName(name);
        route.setRoutePath(routePath);
        route.setPoisIds(extractPoisIds());

        OnDataLoaded<DefaultResponse> callback = new OnDataLoaded<DefaultResponse>() {
            @Override
            public void onSuccess(DefaultResponse defaultResponse) {
                view.hideProgress();
                view.showMyRoutes();
            }

            @Override
            public void onError(Throwable t, @Nullable ResponseBody error) {
                view.hideProgress();
                if (error != null) {
                    try {
                        DefaultResponse defaultResponse = new Gson().fromJson(error.string(), DefaultResponse.class);
                        view.showErrorMessage("Error", defaultResponse.getMessage());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        if (routeToEdit == null) {
            restClient.createRoute(route, tokenRepository.getToken(), callback);
        } else {
            restClient.updateRoute(routeToEdit.getId(), route, tokenRepository.getToken(), callback);
        }
    }

    private List<Long> extractPoisIds() {
        List<Long> poiIds = new ArrayList<>();
        if (waypoints != null) {
            for (int i = 0; i < waypoints.size(); i++) {
                poiIds.add(waypoints.get(i).getId());
            }
        }

        return poiIds;
    }

    public void findPlaces(String newText, String key, final boolean from) {
        restClient.getPlaces(newText, key, new OnDataLoaded<PlacesEntity>() {
            @Override
            public void onSuccess(PlacesEntity placesEntity) {
                view.populateAutocompeteList(from, placesEntity);
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

    public void getDetailedPlaceInfo(String placeId, String key, final boolean isOrigin) {
        view.showProgress();
        restClient.getPlace(placeId, key, new OnDataLoaded<PlaceDetailedEntity>() {
            @Override
            public void onSuccess(PlaceDetailedEntity entity) {
                view.hideProgress();
                if (entity != null) {
                    if (isOrigin) {
                        myLocation = new Location("");
                        myLocation.setLatitude(Double.parseDouble(entity.getResult().getGeometry().getLocation().getLat()));
                        myLocation.setLongitude(Double.parseDouble(entity.getResult().getGeometry().getLocation().getLng()));
                    } else {
                        selectedLocation = new LatLng(Double.parseDouble(entity.getResult().getGeometry().getLocation().getLat()),
                                Double.parseDouble(entity.getResult().getGeometry().getLocation().getLng()));
                    }
                }
                if (myLocation != null && selectedLocation != null) {
                    getPathForCities(myLocation.getLatitude() + "," + myLocation.getLongitude(), selectedLocation.latitude + "," + selectedLocation.longitude);
                }
            }

            @Override
            public void onError(Throwable t, ResponseBody responseBody) {
                view.hideProgress();
                view.showErrorMessage("Error", t.getMessage());
            }
        });
    }

    public void getPoiDetails(LatLng position) {
        Poi poi = new Poi();
        poi.setLatitude((float) position.latitude);
        poi.setLongitude((float) position.longitude);
        int indexOf = poisNearMe.indexOf(poi);
        if (indexOf > -1) {
            Poi selectedPoi = poisNearMe.get(indexOf);
            if (selectedPoi != null) {
                view.showProgress();
                restClient.getPoiDetails(String.valueOf(selectedPoi.getId()), new OnDataLoaded<Poi>() {
                    @Override
                    public void onSuccess(Poi poi) {

                        view.hideProgress();
                        view.setDetailedPoiInfo(poi, isWaypoint(poi));

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
    }

    public interface IPathCallback {
        void onPathCalculated();

        void onError(Exception e);
    }
}
