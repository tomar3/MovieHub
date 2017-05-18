package com.tomar.udacity.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tomar.udacity.popularmovies.MainActivity.MOVIE_INFO;
import static com.tomar.udacity.popularmovies.MainActivity.PHOTO_URL;


public class MovieDetailActivity extends AppCompatActivity {
    @BindView(R.id.tv_year) TextView year;
    @BindView(R.id.tv_movie_title) TextView title;
    @BindView(R.id.tv_rating) TextView rating;
    @BindView(R.id.tv_descr) TextView descr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        //Retrieve the incoming intent
        Intent incomingIntent = getIntent();

        //Set up all of the views with the passed data
        if(incomingIntent != null){

            ImageView movieThumbnail = (ImageView) findViewById(R.id.iv_movie_thumb);

            //Use Picasso to load the photo url
            Picasso.with(this)
                    .load(incomingIntent.getStringExtra(PHOTO_URL))
                    .placeholder(R.drawable.placeholder_loading_image)
                    .error(R.drawable.error_placeholder)
                    .fit()
                    .into(movieThumbnail);

            //Set the text views based on the passed data
            MovieInfo movieInfo = incomingIntent.getParcelableExtra(MOVIE_INFO);

            title.setText(movieInfo.title);
            year.setText(movieInfo.year);

            String ratingConcat = movieInfo.rating + "/10";
            rating.setText(ratingConcat);
            descr.setText(movieInfo.descr);


        }else{
            finish();
        }
    }
}
