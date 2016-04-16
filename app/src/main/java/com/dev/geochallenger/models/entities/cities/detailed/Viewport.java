package com.dev.geochallenger.models.entities.cities.detailed;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Viewport {
    private Southwest southwest;

    private Northeast northeast;

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }
}
