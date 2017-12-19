package com.codertal.moviehub.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.codertal.moviehub.BuildConfig;
import com.codertal.moviehub.data.movies.remote.MovieService;
import com.codertal.moviehub.data.movies.remote.RetrofitMovieClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String MOVIE_SEARCH_POPULAR_URL = "http://api.themoviedb.org/3/movie/popular";
    private final static String MOVIE_SEARCH_TOP_URL = "http://api.themoviedb.org/3/movie/top_rated";
    private final static String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private final static String MOVIE_IMAGE_BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w780";
    private final static String MOVIE_DETAILS_BASE_START = "https://api.themoviedb.org/3/movie";
    private final static String PARAM_QUERY = "api_key";
    private final static String PARAM_APPEND_QUERY = "append_to_response";
    private final static String RESULT_APPEND = "videos,reviews";

    private final static String YOUTUBE_BASE_URL = "https://www.youtube.com/watch";
    private final static String YOUTUBE_PARAM_QUERY = "v";




    public static String buildPosterUrl(String imageExtension) {
        //Append the image extension path to the base url for images
        return MOVIE_IMAGE_BASE_URL + "/" + imageExtension;
    }

    public static String buildBackdropUrl(String imageExtension) {
        //Append the image extension path to the base url for images
        return MOVIE_IMAGE_BASE_BACKDROP_URL + "/" + imageExtension;
    }


    public static String buildSearchUrl(String baseURL){
        //Build the base url with the api key
        Uri builtUri = Uri.parse(baseURL).buildUpon()
                .appendQueryParameter(PARAM_QUERY, BuildConfig.MOVIE_DB_API_KEY)
                .build();

        return builtUri.toString();
    }

    public static URL buildMovieDetailUrl(String movieId){
        //Build the movie detail url with the api key and movie id
        Uri builtUri = Uri.parse(MOVIE_DETAILS_BASE_START).buildUpon()
                .appendPath(movieId)
                .appendQueryParameter(PARAM_QUERY, BuildConfig.MOVIE_DB_API_KEY)
                .appendQueryParameter(PARAM_APPEND_QUERY, RESULT_APPEND)
                .build();
        return convertToURL(builtUri.toString());
    }

    public static String buildYouTubeUrl(String movieKey){
        //Build the youtube video url with the trailer key
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_PARAM_QUERY, movieKey)
                .build();
        return builtUri.toString();
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

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
