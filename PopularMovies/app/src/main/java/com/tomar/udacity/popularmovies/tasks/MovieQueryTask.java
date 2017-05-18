package com.tomar.udacity.popularmovies.tasks;


import android.os.AsyncTask;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;

//AsyncTask to query the movie api
 public class MovieQueryTask extends AsyncTask<URL, Void, String> {
    private OnMovieQueryExecuteListener movieQueryExecuteListener;

    public interface OnMovieQueryExecuteListener{
        public void onMovieQueryPreExecute();
        public void onMovieQueryPostExecute(String movieSearchResults);
    }

    public MovieQueryTask(OnMovieQueryExecuteListener movieQueryExecuteListener){
        this.movieQueryExecuteListener = movieQueryExecuteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        movieQueryExecuteListener.onMovieQueryPreExecute();
    }

    @Override
    protected String doInBackground(URL... params) {
        //Execute http request on the given url
        URL movieSearchUrl = params[0];
        String movieSearchResults = null;
        try {
            movieSearchResults = NetworkUtils.getResponseFromHttpUrl(movieSearchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return movieSearchResults;
    }

    @Override
    protected void onPostExecute(String movieSearchResults) {
        movieQueryExecuteListener.onMovieQueryPostExecute(movieSearchResults);
    }

}