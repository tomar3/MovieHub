package com.codertal.moviehub.data.videos.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Video {

    @SerializedName("key")
    @Expose
    private String key;

    @SerializedName("name")
    @Expose
    private String name;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
