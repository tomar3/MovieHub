package com.codertal.moviehub.model;


import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Movie {
    String title, rating, year, descr, movieId, posterURL, backdropURL;

    @ParcelConstructor
    public Movie(String title, String rating, String year, String descr, String movieId,
                 String posterURL, String backdropURL) {
        this.title = title;
        this.rating = rating;
        this.year = year.split("-")[0];
        this.descr = descr;
        this.movieId = movieId;
        this.posterURL = posterURL;
        this.backdropURL = backdropURL;
    }

    public String getTitle() {
        return title;
    }

    public String getRating() {
        return rating;
    }

    public String getYear() {
        return year;
    }

    public String getDescr() {
        return descr;
    }

    public String getMovieId() {
        return movieId;
    }

    public String getPosterURL() {
        return posterURL;
    }

    public String getBackdropURL() {
        return backdropURL;
    }
}
