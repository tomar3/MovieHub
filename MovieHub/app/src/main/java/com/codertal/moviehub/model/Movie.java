package com.codertal.moviehub.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable {
    public String title, rating, year, descr, movieId, posterURL, backdropURL;

    public Movie(String title, String rating, String date, String descr, String movieId,
                 String posterURL, String backdropURL) {
        this.title = title;
        this.rating = rating;
        this.year = date.split("-")[0];
        this.descr = descr;
        this.movieId = movieId;
        this.posterURL = posterURL;
        this.backdropURL = backdropURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.rating);
        dest.writeString(this.year);
        dest.writeString(this.descr);
        dest.writeString(this.movieId);
        dest.writeString(this.posterURL);
        dest.writeString(this.backdropURL);
    }

    protected Movie(Parcel in) {
        this.title = in.readString();
        this.rating = in.readString();
        this.year = in.readString();
        this.descr = in.readString();
        this.movieId = in.readString();
        this.posterURL = in.readString();
        this.backdropURL = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
