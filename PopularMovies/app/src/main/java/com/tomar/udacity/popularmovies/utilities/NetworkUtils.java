package com.tomar.udacity.popularmovies.utilities;

import android.net.Uri;

import com.tomar.udacity.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {
    private final static String MOVIE_SEARCH_POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";
    private final static String MOVIE_SEARCH_TOP_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private final static String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private final static String PARAM_QUERY = "api_key";


    public static String buildImageUrl(String imageExtension) {
        //Append the image extension path to the base url for images
        return MOVIE_IMAGE_BASE_URL + "/" + imageExtension;
    }

    public static URL buildSearchUrl(String baseURL){
        //Build the base url with the api key
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return convertToURL(builtUri.toString());
    }

    //Helper method to convert a string into a url object
    private static URL convertToURL(String builtUri){
        URL url = null;
        try {
            url = new URL(builtUri);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        //Create the http connection and get the input stream from the given url
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if (scanner.hasNext()) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static String getMovieSearchPopularUrl() {
        return MOVIE_SEARCH_POPULAR_URL;
    }

    public static String getMovieSearchTopUrl() {
        return MOVIE_SEARCH_TOP_URL;
    }
}
