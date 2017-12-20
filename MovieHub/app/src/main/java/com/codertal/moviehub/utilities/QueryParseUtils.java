package com.codertal.moviehub.utilities;

import android.database.Cursor;

import com.codertal.moviehub.data.movies.local.MovieContract;
import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.reviews.Review;
import com.codertal.moviehub.data.trailers.Trailer;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QueryParseUtils {

    public static List<Movie> parseMovieFavoriteQuery(Cursor cursor){
        List<Movie> movies = new ArrayList<>(cursor.getCount());

        if(cursor.getCount() < 1){
           return null;
        }else {

            while (cursor.moveToNext()) {

                //Store movie stats and info
                movies.add(new Movie(
                        cursor.getInt(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID)),
                        cursor.getDouble(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_URL)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_BACKDROP_URL)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCR)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_YEAR))
                ));

            }

           return movies;
        }
    }

    public static boolean parseMovieDetailQuery(JSONObject movieDetailJSONResult, ArrayList<Trailer> trailers,
                                                ArrayList<Review> reviews){
        boolean success;

        try{

            JSONObject videosObject = movieDetailJSONResult.getJSONObject("videos");
            JSONArray trailersArray = videosObject.getJSONArray("results");

            //Iterate to each trailer object and store its necessary info
            for(int i = 0; i<trailersArray.length(); i++){
                JSONObject trailerInfo = trailersArray.getJSONObject(i);

                if(trailerInfo.getString("type").equals("Trailer")){
                    trailers.add(new Trailer(trailerInfo.getString("name"), trailerInfo.getString("key")));
                }
            }

            JSONObject reviewsObject = movieDetailJSONResult.getJSONObject("reviews");
            JSONArray reviewsArray = reviewsObject.getJSONArray("results");

            //Iterate to each review object and store its necessary info
            for(int i = 0; i<reviewsArray.length(); i++){
                JSONObject reviewInfo = reviewsArray.getJSONObject(i);

                reviews.add(new Review(reviewInfo.getString("author"), reviewInfo.getString("content")));
            }


            success = true;

        }
        catch (Exception e){
            e.printStackTrace();
            success = false;
        }

        return success;
    }
}
