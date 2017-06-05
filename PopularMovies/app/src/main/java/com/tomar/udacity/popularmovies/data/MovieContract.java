package com.tomar.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    public static final String AUTHORITY = "com.tomar.udacity.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAV_MOVIES = "favMovies";

    //Defines the contents of the favorite movies table
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIES).build();


        // Favorite Movies table and column names
        public static final String TABLE_NAME = "favorite_movies";


        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_DESCR = "descr";
        public static final String COLUMN_POSTER_URL = "posterUrl";
        public static final String COLUMN_BACKDROP_URL = "backdropUrl";
        public static final String COLUMN_MOVIE_ID = "movieID";

    }
}
