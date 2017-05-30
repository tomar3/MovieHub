package com.tomar.udacity.popularmovies.tasks;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import static com.tomar.udacity.popularmovies.MovieDetailActivity.MOVIE_ID_KEY;

public class TrailerQueryLoader extends AsyncTaskLoader<String> {
    private String movieId;
    private String mMovieDetailResults;

    public TrailerQueryLoader(Context context, Bundle args){
        super(context);
        movieId = args.getString(MOVIE_ID_KEY);
    }

    @Override
    protected void onStartLoading() {
        if (mMovieDetailResults == null) {
            forceLoad();
        }else {
            deliverResult(mMovieDetailResults);
        }

    }

    @Override
    public String loadInBackground() {
        try {
            URL movieDetailQueryUrl = NetworkUtils.buildMovieDetailUrl(movieId);
            return NetworkUtils.getResponseFromHttpUrl(movieDetailQueryUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deliverResult(String movieDetailJson) {
        mMovieDetailResults = movieDetailJson;
        super.deliverResult(movieDetailJson);
    }
}
