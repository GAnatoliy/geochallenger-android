package com.dev.geochallenger.models.entities.routes;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 23.04.2016.
 */
public class RouteResponse {
    private int id;
    private String name;
    private double startPointLatitude;
    private double startPointLongitude;
    private double endPointLatitude;
    private double endPointLongitude;
    private double distanceInMeters;
    private String routePath;
    private List<Long> pois;

    public String getName() {
        return name;
    }

    public double getStartPointLatitude() {
        return startPointLatitude;
    }

    public double getStartPointLongitude() {
        return startPointLongitude;
    }

    public double getEndPointLatitude() {
        return endPointLatitude;
    }

    public double getEndPointLongitude() {
        return endPointLongitude;
    }

    public double getDistanceInMeters() {
        return distanceInMeters;
    }

    public String getRoutePath() {
        return routePath;
    }

    public List<Long> getPois() {
        return pois;
    }

    public int getId() {
        return id;
    }
}
