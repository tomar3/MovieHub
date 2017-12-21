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
