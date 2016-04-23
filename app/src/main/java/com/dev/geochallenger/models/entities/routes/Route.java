package com.dev.geochallenger.models.entities.routes;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 22.04.2016.
 */
public class Route {

    private String name;
    private double startPointLatitude;
    private double startPointLongitude;
    private double endPointLatitude;
    private double endPointLongitude;
    private double distanceInMeters;
    private String routePath;
    private List<Long> poisIds;

    public void setName(String name) {
        this.name = name;
    }

    public void setStartPointLatitude(double startPointLatitude) {
        this.startPointLatitude = startPointLatitude;
    }

    public void setStartPointLongitude(double startPointLongitude) {
        this.startPointLongitude = startPointLongitude;
    }

    public void setEndPointLatitude(double endPointLatitude) {
        this.endPointLatitude = endPointLatitude;
    }

    public void setEndPointLongitude(double endPointLongitude) {
        this.endPointLongitude = endPointLongitude;
    }

    public void setDistanceInMeters(double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    public void setRoutePath(String routePath) {
        this.routePath = routePath;
    }

    public void setPoisIds(List<Long> poisIds) {
        this.poisIds = poisIds;
    }

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

    public List<Long> getPoisIds() {
        return poisIds;
    }
}
