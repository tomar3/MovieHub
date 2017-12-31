package com.codertal.moviehub.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import com.codertal.moviehub.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private final static String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w342";
    private final static String MOVIE_IMAGE_BASE_BACKDROP_URL = "http://image.tmdb.org/t/p/w780";

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

    public static String buildYouTubeUrl(String movieKey){
        //Build the youtube video url with the trailer key
        Uri builtUri = Uri.parse(YOUTUBE_BASE_URL).buildUpon()
                .appendQueryParameter(YOUTUBE_PARAM_QUERY, movieKey)
                .build();
        return builtUri.toString();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
