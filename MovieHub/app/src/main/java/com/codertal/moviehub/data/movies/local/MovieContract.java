package com.codertal.moviehub.data.movies.local;

import android.net.Uri;
import android.provider.BaseColumns;

import com.codertal.moviehub.BuildConfig;

public class MovieContract {

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAV_MOVIES = "favMovies";

    //Defines the contents of the favorite movies table
    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAV_MOVIES).build();


        // Favorite Movies table and column names
        public static final String TABLE_NAME = "favorite_movies";

        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_VOTE_AVERAGE = "voteAverage";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "posterPath";
        public static final String COLUMN_BACKDROP_PATH = "backdropPath";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_YEAR = "year";

    }
}
