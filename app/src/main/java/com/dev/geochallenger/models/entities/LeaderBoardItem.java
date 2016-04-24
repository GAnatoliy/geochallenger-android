package com.dev.geochallenger.models.entities;

/**
 * Created by a_dibrivnyj on 4/24/16.
 */
public class LeaderBoardItem {
    private int id;
    private String email;
    private String name;
    private int points;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
