package com.codertal.moviehub.data.movies.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class Movie {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("poster_path")
    @Expose
    private String posterPath;

    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;

    @SerializedName("overview")
    @Expose
    private String overview;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    public Movie() {
    }

    public Movie(Integer id, Double voteAverage, String title, String posterPath,
                 String backdropPath, String overview, String releaseDate) {
        this.id = id;
        this.voteAverage = voteAverage;
        this.title = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.overview = overview;
        this.releaseDate = releaseDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

}
