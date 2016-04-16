package com.dev.geochallenger.models.entities.cities;

/**
 * Created by a_dibrivnyj on 4/16/16.
 */
public class Predictions {
    private String id;

    private String place_id;

    private Matched[] matched_substrings;

    private String description;

    private Terms[] terms;

    private String[] types;

    private String reference;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public Matched[] getMatched_substrings() {
        return matched_substrings;
    }

    public void setMatched_substrings(Matched[] matched_substrings) {
        this.matched_substrings = matched_substrings;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Terms[] getTerms() {
        return terms;
    }

    public void setTerms(Terms[] terms) {
        this.terms = terms;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

}
