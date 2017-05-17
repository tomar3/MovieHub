package com.tomar.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.tomar.udacity.popularmovies.MainActivity.DESCR;
import static com.tomar.udacity.popularmovies.MainActivity.PHOTO_URL;
import static com.tomar.udacity.popularmovies.MainActivity.RATING;
import static com.tomar.udacity.popularmovies.MainActivity.TITLE;
import static com.tomar.udacity.popularmovies.MainActivity.YEAR;

public class MovieDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Retrieve the incoming intent
        Intent incomingIntent = getIntent();

        //Set up all of the views with the passed data
        if(incomingIntent != null){

            ImageView movieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumb);

            //Use Picasso to load the photo url
            Picasso.with(this)
                    .load(incomingIntent.getStringExtra(PHOTO_URL))
                    .fit()
                    .into(movieThumbnail);

            //Set the text views based on the passed data
            TextView title = (TextView) findViewById(R.id.tv_movie_title);
            title.setText(incomingIntent.getStringExtra(TITLE));

            TextView year = (TextView) findViewById(R.id.tv_year);
            year.setText(incomingIntent.getStringExtra(YEAR));

            TextView rating = (TextView) findViewById(R.id.tv_rating);
            String ratingConcat = incomingIntent.getStringExtra(RATING) + "/10";
            rating.setText(ratingConcat);

            TextView descr = (TextView) findViewById(R.id.tv_descr);
            descr.setText(incomingIntent.getStringExtra(DESCR));


        }else{
            finish();
        }
    }
}
