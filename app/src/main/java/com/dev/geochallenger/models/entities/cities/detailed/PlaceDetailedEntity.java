package com.dev.geochallenger.models.entities.cities.detailed;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class PlaceDetailedEntity {
    private Result result;

    private String[] html_attributions;

    private String status;

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String[] getHtml_attributions() {
        return html_attributions;
    }

    public void setHtml_attributions(String[] html_attributions) {
        this.html_attributions = html_attributions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
