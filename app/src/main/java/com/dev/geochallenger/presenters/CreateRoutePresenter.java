package com.dev.geochallenger.presenters;

import android.location.Address;
import android.location.Location;
import android.support.annotation.Nullable;

import com.dev.geochallenger.models.entities.DefaultResponse;
import com.dev.geochallenger.models.entities.Poi;
import com.dev.geochallenger.models.entities.cities.PlacesEntity;
import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.entities.directions.Leg;
import com.dev.geochallenger.models.entities.directions.Route;
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
    private List<LatLng> waypoints = new ArrayList<>();
    private String source;
    private String destination;
    private double routeDistance;
    private Location myLocation;
    private ITokenRepository tokenRepository;
    private LatLng selectedLocation;
    private Address selectedAddress;
    private String routePath;
    private List<Poi> waypointPois;

    public CreateRoutePresenter(ICreateRouteView view, IModel restClient, LatLng selectedLocation,
                                Address selectedAddress, Location myLocation, ITokenRepository tokenRepository) {
        super(view);
        this.restClient = restClient;
        this.selectedLocation = selectedLocation;
        this.selectedAddress = selectedAddress;
        this.myLocation = myLocation;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void init() {
        view.initMap();
        if (selectedLocation != null && myLocation != null) {

            getPathForCities(myLocation.getLatitude() + "," + myLocation.getLongitude(), selectedLocation.latitude + "," + selectedLocation.longitude);
        }
        if (myLocation != null) {
            view.setOrigin("My location");
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
            LatLng latLng = waypoints.get(i);
            //convertedString.append("via:");
            convertedString.append(latLng.latitude);
            convertedString.append(",");
            convertedString.append(latLng.longitude);
            convertedString.append("|");
        }

        return convertedString.length() > 0 ? convertedString.substring(0, convertedString.length() - 1) : null;
    }

    public void toggleWaypoints(LatLng point) {
        if (waypoints.contains(point)) {
            waypoints.remove(point);
        } else {
            waypoints.add(point);
        }

        getPathForCities(source, destination);
    }

    private void parseDirections(final GoogleDirectionsEntity entity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    List<Route> entityRoutes = entity.getRoutes();


                    /** Traversing all routes */
                    if (entityRoutes != null) {
                        for (int i = 0; i < entityRoutes.size(); i++) {
                            routePath += entityRoutes.get(i).getOverviewPolyline().getPoints();
                        }
                    }

                    List<List<HashMap<String, String>>> routes = new DirectionsJSONParser().parse(entity);

                    ArrayList<LatLng> points = null;
                    PolylineOptions lineOptions = new PolylineOptions();
                    MarkerOptions markerOptions = new MarkerOptions();
                    double distance = 0;
                    String duration = "";

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

                    routeDistance = distance;
                    view.drawRouteInUiThread(lineOptions, distance * 0.001);
                } catch (Exception e) {
                    e.printStackTrace();
                    view.hideProgressInUiThread();
                }
            }
        }).start();
    }

    public void getPoisByViewPort(Double topLeftLatitude, Double topLeftLongitude, Double bottomRightLatitude, Double bottomRightLongitude) {
        restClient.getPoiList(topLeftLatitude, topLeftLongitude, bottomRightLatitude, bottomRightLongitude, new OnDataLoaded<List<Poi>>() {
            @Override
            public void onSuccess(List<Poi> pois) {
                waypointPois = pois;
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

        restClient.createRoute(route, tokenRepository.getToken(), new OnDataLoaded<DefaultResponse>() {
            @Override
            public void onSuccess(DefaultResponse defaultResponse) {
                view.hideProgress();
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
        });
    }

    private List<Long> extractPoisIds() {
        List<Long> poiIds = new ArrayList<>();
        if (waypointPois != null) {
            for (int i = 0; i < waypointPois.size(); i++) {
                poiIds.add(waypointPois.get(i).getId());
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

    public interface IPathCallback {
        void onPathCalculated();

        void onError(Exception e);
    }
}
