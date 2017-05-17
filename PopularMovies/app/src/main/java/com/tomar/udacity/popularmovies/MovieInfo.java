package com.tomar.udacity.popularmovies;

public class MovieInfo {
    String title, rating, year, descr;

    public MovieInfo(String title, String rating, String date, String descr ) {
        this.title = title;
        this.rating = rating;
        this.year = date.split("-")[0];
        this.descr = descr;
    }
}
