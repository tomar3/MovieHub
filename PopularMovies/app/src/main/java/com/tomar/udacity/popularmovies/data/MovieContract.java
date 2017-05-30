package com.tomar.udacity.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieContract {

    // The authority, which is how your code knows which Content Provider to access
    public static final String AUTHORITY = "com.tomar.udacity.popularmovies";

    // The base content URI = "content://" + <authority>
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Define the possible paths for accessing data in this contract
    // This is the path for the "tasks" directory
    public static final String PATH_FAV_MOVIES = "favMovies";

    /* TaskEntry is an inner class that defines the contents of the task table */
    public static final class MovieEntry implements BaseColumns {

        // TaskEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIES).build();


        // Task table and column names
        public static final String TABLE_NAME = "favorite_movies";


        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_YEAR = "year";
        public static final String COLUMN_DESCR = "descr";
        public static final String COLUMN_PHOTO_URL = "photoUrl";
        public static final String COLUMN_MOVIE_ID = "movieID";



    }
}
