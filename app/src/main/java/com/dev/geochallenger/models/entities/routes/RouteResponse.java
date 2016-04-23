package com.dev.geochallenger.models.entities.routes;

import com.dev.geochallenger.models.entities.Poi;
import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Created by Yuriy Diachenko on 23.04.2016.
 */
public class RouteResponse {
    private int id;
    private String name;
    private double startPointLatitude;
    private double startPointLongitude;

    @Expose
    private String startAddress;
    private double endPointLatitude;
    private double endPointLongitude;

    @Expose
    private String endAddress;
    private double distanceInMeters;
    private String routePath;
    private List<Poi> pois;

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

    public List<Poi> getPois() {
        return pois;
    }

    public int getId() {
        return id;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }
}
