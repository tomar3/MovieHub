package com.codertal.moviehub.utilities;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.local.MovieContract;

import java.util.ArrayList;
import java.util.List;

public class FavoriteMovieParser {
    private final Cursor mCursor;

    public FavoriteMovieParser(@NonNull Cursor cursor) {
        mCursor = cursor;
    }

    public List<Movie> parse(){
        List<Movie> movies = new ArrayList<>(mCursor.getCount());

        if(mCursor.getCount() < 1){
            return null;
        }else {

            while (mCursor.moveToNext()) {

                //Store movie stats and info
                movies.add(new Movie(
                        mCursor.getInt(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)),
                        mCursor.getDouble(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE)),
                        mCursor.getString(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        mCursor.getString(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH)),
                        mCursor.getString(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH)),
                        mCursor.getString(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW)),
                        mCursor.getString(
                                mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_YEAR))
                ));

            }

            return movies;
        }
    }
}
