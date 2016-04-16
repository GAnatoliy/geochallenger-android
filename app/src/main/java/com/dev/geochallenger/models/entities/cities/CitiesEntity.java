package com.dev.geochallenger.models.entities.cities;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class CitiesEntity {
    private Predictions[] predictions;

    private String status;

    public Predictions[] getPredictions() {
        return predictions;
    }

    public void setPredictions(Predictions[] predictions) {
        this.predictions = predictions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
