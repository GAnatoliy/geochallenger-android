package com.dev.geochallenger.models.entities.cities.detailed;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Access_points {
    private String[] travel_modes;

    private Location location;

    public String[] getTravel_modes() {
        return travel_modes;
    }

    public void setTravel_modes(String[] travel_modes) {
        this.travel_modes = travel_modes;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
