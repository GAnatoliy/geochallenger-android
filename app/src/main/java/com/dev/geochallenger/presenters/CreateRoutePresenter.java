package com.dev.geochallenger.presenters;

import android.view.View;
import android.widget.Toast;

import com.dev.geochallenger.models.entities.directions.GoogleDirectionsEntity;
import com.dev.geochallenger.models.interfaces.IModel;
import com.dev.geochallenger.models.interfaces.OnDataLoaded;
import com.dev.geochallenger.models.parsers.DirectionsJSONParser;
import com.dev.geochallenger.presenters.interfaces.IPresenter;
import com.dev.geochallenger.views.interfaces.ICreateRouteView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Yuriy Diachenko on 16.04.2016.
 */
public class CreateRoutePresenter extends IPresenter<ICreateRouteView> {

    private IModel restClient;
    private List<LatLng> waypoints = new ArrayList<>();
    private String source;
    private String destination;

    public CreateRoutePresenter(ICreateRouteView view, IModel restClient) {
        super(view);
        this.restClient = restClient;
    }

    @Override
    public void init() {
        view.initMap();
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
            public void onError(Throwable t) {
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

        return convertedString.length() > 0 ? convertedString.substring(0, convertedString.length() - 1): null;
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
                    List<List<HashMap<String, String>>> routes = new DirectionsJSONParser().parse(entity);

                    ArrayList<LatLng> points = null;
                    PolylineOptions lineOptions = new PolylineOptions();
                    MarkerOptions markerOptions = new MarkerOptions();
                    String distance = "";
                    String duration = "";

                    // Traversing through all the routes
                    for (int i = 0; i < routes.size(); i++) {
                        points = new ArrayList<LatLng>();

                        // Fetching i-th route
                        List<HashMap<String, String>> path = routes.get(i);

                        // Fetching all the points in i-th route
                        for (int j = 0; j < path.size(); j++) {
                            HashMap<String, String> point = path.get(j);

                            if (j == 0) {    // Get distance from the list
                                distance = (String) point.get("distance");
                                continue;
                            } else if (j == 1) { // Get duration from the list
                                duration = (String) point.get("duration");
                                continue;
                            }

                            double lat = Double.parseDouble(point.get("lat"));
                            double lng = Double.parseDouble(point.get("lng"));
                            LatLng position = new LatLng(lat, lng);

                            points.add(position);
                        }

                        // Adding all the points in the route to LineOptions
                        lineOptions.addAll(points);
                    }

                    // Drawing polyline in the Google Map for the i-th route
                    view.drawRouteInUiThread(lineOptions);
                } catch (Exception e) {
                    e.printStackTrace();
                    view.hideProgressInUiThread();
                }
            }
        }).start();
    }

    public interface IPathCallback {
        void onPathCalculated();
        void onError(Exception e);
    }
}
