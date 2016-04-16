package com.dev.geochallenger.models.entities;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Poi {

    private int id;
    private String title;
    private String address;
    private float latitude;
    private float longitude;

    public int getPoiId() {
        return id;
    }

    public void setPoiId(int poiId) {
        this.id = poiId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }
}
