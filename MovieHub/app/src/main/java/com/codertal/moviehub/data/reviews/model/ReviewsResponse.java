package com.codertal.moviehub.data.reviews.model;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReviewsResponse {

    @SerializedName("results")
    @Expose
    private List<Review> results = null;


    public List<Review> getResults() {
        return results;
    }

    public void setResults(List<Review> results) {
        this.results = results;
    }
}