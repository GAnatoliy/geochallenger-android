package com.dev.geochallenger.models.entities;

import com.google.gson.annotations.Expose;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Poi {

    private long id;
    private String title;
    private String address;
    private float latitude;
    private float longitude;

    private String contentPreview;
    private String createdAtUtc;
    private int ownerId;

    private String content;

    @Expose
    private boolean isWaypoint;

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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getContentPreview() {
        return contentPreview;
    }

    public void setContentPreview(String contentPreview) {
        this.contentPreview = contentPreview;
    }

    public String getCreatedAtUtc() {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc) {
        this.createdAtUtc = createdAtUtc;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Poi) {
            if (((Poi) o).longitude == this.longitude
                    && ((Poi) o).latitude == this.latitude) {
                return true;
            }
        }

        return false;
    }

    public boolean isWaypoint() {
        return isWaypoint;
    }

    public void setWaypoint(boolean waypoint) {
        isWaypoint = waypoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

