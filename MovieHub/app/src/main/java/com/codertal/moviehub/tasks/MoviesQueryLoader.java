package com.codertal.moviehub.tasks;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;

import com.codertal.moviehub.utilities.NetworkUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static com.codertal.moviehub.fragments.MoviesGridFragment.MOVIES_SEARCH_URL_KEY;

public class MoviesQueryLoader extends AsyncTaskLoader<String> {
    private URL mMovieSearchURL;
    private String mMoviesSearchResults;

    public MoviesQueryLoader(Context context, Bundle args){
        super(context);
        try{
            mMovieSearchURL = new URL(args.getString(MOVIES_SEARCH_URL_KEY));
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStartLoading() {
        //Use cached results if previously loaded
        if (mMoviesSearchResults == null) {
            forceLoad();
        }else {
            deliverResult(mMoviesSearchResults);
        }

    }

    @Override
    public String loadInBackground() {
        //Execute http request on the given url
        String movieSearchResults = null;
        try {
            movieSearchResults = NetworkUtils.getResponseFromHttpUrl(mMovieSearchURL);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieSearchResults;
    }

    @Override
    public void deliverResult(String moviesSearchJson) {
        mMoviesSearchResults = moviesSearchJson;
        super.deliverResult(moviesSearchJson);
    }
}
