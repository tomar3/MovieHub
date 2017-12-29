package com.codertal.moviehub.data.videos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideosResponse {

    @SerializedName("results")
    @Expose
    private List<Video> results = null;

    public List<Video> getResults() {
        return results;
    }

    public void setResults(List<Video> results) {
        this.results = results;
    }

}