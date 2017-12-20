package com.codertal.moviehub.tasks;

import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;

import com.codertal.moviehub.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

import static com.codertal.moviehub.features.moviedetail.MovieDetailActivity.MOVIE_ID_KEY;

public class MovieDetailsQueryLoader extends AsyncTaskLoader<String> {
    private String movieId;
    private String mMovieDetailResults;

    public MovieDetailsQueryLoader(Context context, Bundle args){
        super(context);
        movieId = args.getString(MOVIE_ID_KEY);
    }

    @Override
    protected void onStartLoading() {
        //Use cached results if previously loaded
        if (mMovieDetailResults == null) {
            forceLoad();
        }else {
            deliverResult(mMovieDetailResults);
        }

    }

    @Override
    public String loadInBackground() {
        try {
            //Query http url for movie detail json
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
