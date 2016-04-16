package com.dev.geochallenger.models.entities.cities.detailed;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Geometry {
    private Viewport viewport;

    private Location location;

    private Access_points[] access_points;

    public Viewport getViewport ()
    {
        return viewport;
    }

    public void setViewport (Viewport viewport)
    {
        this.viewport = viewport;
    }

    public Location getLocation ()
    {
        return location;
    }

    public void setLocation (Location location)
    {
        this.location = location;
    }

    public Access_points[] getAccess_points ()
    {
        return access_points;
    }

    public void setAccess_points (Access_points[] access_points)
    {
        this.access_points = access_points;
    }

}
