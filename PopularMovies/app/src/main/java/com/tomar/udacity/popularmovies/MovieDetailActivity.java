package com.tomar.udacity.popularmovies;


import android.content.ContentValues;
import android.content.Intent;


import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.tomar.udacity.popularmovies.adapters.MovieGridAdapter;
import com.tomar.udacity.popularmovies.adapters.ReviewListAdapter;
import com.tomar.udacity.popularmovies.adapters.TrailerListAdapter;
import com.tomar.udacity.popularmovies.data.MovieContract;
import com.tomar.udacity.popularmovies.tasks.MovieFavoritesQueryHandler;
import com.tomar.udacity.popularmovies.tasks.TrailerQueryLoader;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.MOVIE_INFO;
import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.PHOTO_URL;


public class MovieDetailActivity extends AppCompatActivity implements
        TrailerListAdapter.ListItemClickListener ,
        LoaderManager.LoaderCallbacks<String> ,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener{

    @BindView(R.id.tv_year) TextView year;
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.tv_rating) TextView rating;
    @BindView(R.id.tv_descr) TextView descr;
    @BindView(R.id.fab_fav) FloatingActionButton fabFavorite;
    @BindView(R.id.iv_movie_thumb) ImageView movieThumbnail;
    @BindView(R.id.iv_movie_background) ImageView movieBackground;

    public static final String MOVIE_ID_KEY = "movieId";
    private static final int MOVIE_DETAIL_LOADER = 11;
    private static final int MOVIE_FAV_QUERY = 10;
    private static final int MOVIE_FAV_INSERT = 9;
    private static final int MOVIE_FAV_DELETE = 8;

    private RecyclerView mTrailerList;
    private RecyclerView mReviewList;
    private ArrayList<String> mReviews;
    private ArrayList<String> mTrailerTitles;
    private ArrayList<String> mTrailerKeys;

    private TrailerListAdapter mTrailerListAdapter;
    private ReviewListAdapter mReviewListAdapter;

    private boolean isFavorited;
    private MovieInfo mMovieInfo;
    private String mPhotoUrl;
    private MovieFavoritesQueryHandler movieFavoritesQueryHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        movieFavoritesQueryHandler = new MovieFavoritesQueryHandler(getContentResolver(), this);

        mTrailerTitles = new ArrayList<>();
        mTrailerKeys = new ArrayList<>();
        mReviews = new ArrayList<>();

        //Retrieve the incoming intent
        Intent incomingIntent = getIntent();

        //Set up all of the views with the passed data
        if(incomingIntent != null){

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(MovieDetailActivity.this);
                }
            });

            mPhotoUrl = incomingIntent.getStringExtra(PHOTO_URL);

            //Use Picasso to load the photo url
            Picasso.with(this)
                    .load(mPhotoUrl)
                    .placeholder(R.drawable.placeholder_loading_image)
                    .error(R.drawable.error_placeholder)
                    .fit()
                    .centerCrop()
                    .into(movieThumbnail);

            //Set the text views based on the passed data
            MovieInfo movieInfo = incomingIntent.getParcelableExtra(MOVIE_INFO);


            year.setText(movieInfo.year);
            String ratingConcat = movieInfo.rating + "/10";
            rating.setText(ratingConcat);
            descr.setText(movieInfo.descr);

            mTrailerList = (RecyclerView) findViewById(R.id.rv_trailers);
            mReviewList = (RecyclerView) findViewById(R.id.rv_reviews);

            //Create new grid layout manager and set it with the recycler view
            mTrailerList.setLayoutManager(new LinearLayoutManager(this));

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            mReviewList.setLayoutManager(linearLayoutManager);

            mTrailerList.setHasFixedSize(true);
            mReviewList.setHasFixedSize(true);

            //Create and set the trailer list adapter
            mTrailerListAdapter = new TrailerListAdapter(mTrailerTitles, this);
            mTrailerList.setAdapter(mTrailerListAdapter);
            mTrailerList.setNestedScrollingEnabled(false);

            mReviewListAdapter = new ReviewListAdapter(mReviews);
            mReviewList.setAdapter(mReviewListAdapter);
            mReviewList.setNestedScrollingEnabled(false);

            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mReviewList.getContext(),
                    linearLayoutManager.getOrientation());
            mReviewList.addItemDecoration(dividerItemDecoration);

            Bundle movieIdBundle = new Bundle();
            movieIdBundle.putString(MOVIE_ID_KEY, movieInfo.movieId);
            getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER, movieIdBundle, this);


            mMovieInfo = movieInfo;
            checkIfFavorited();

        }else{
            finish();
        }
    }

    @Override
    public void onListItemClick(int position) {
        String trailerKey = mTrailerKeys.get(position);
        Intent trailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(NetworkUtils.buildYouTubeUrl(trailerKey)));
        trailerIntent.addCategory(Intent.CATEGORY_BROWSABLE);

        startActivity(trailerIntent);

    }

    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        return new TrailerQueryLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (null == data) {
            //showErrorMessage();
        } else {
            try {

                //Load the movie information from the returned JSON object
                JSONObject movieDetailJsonResult = new JSONObject(data);
                parseMovieDetailJSON(movieDetailJsonResult);

            } catch (Throwable t) {
                Log.e("DetailActivity", "Bad JSON: "  + data );
                //displayResults(false);
            }
            //showJsonDataView();
        }
    }




    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    public void onFavButtonClick(View v) {
        if(isFavorited){
            //delete
            removeFromFavorites();
        }else {
            //insert
            addToFavorites();
        }
    }

    private void parseMovieDetailJSON(JSONObject movieDetailJSON){
        //Load each movie's information from json object retrieved from http request
        try{

            mTrailerTitles.clear();
            mTrailerKeys.clear();
            mReviews.clear();

            JSONObject videosObject = movieDetailJSON.getJSONObject("videos");
            JSONArray trailersArray = videosObject.getJSONArray("results");

            //Iterate to each movie object and store its necessary info
            for(int i = 0; i<trailersArray.length(); i++){
                JSONObject trailerInfo = trailersArray.getJSONObject(i);

                if(trailerInfo.getString("type").equals("Trailer")){
                    mTrailerTitles.add(trailerInfo.getString("name"));
                    mTrailerKeys.add(trailerInfo.getString("key"));
                }
            }

            mTrailerListAdapter.updateData(mTrailerTitles);


            JSONObject reviewsObject = movieDetailJSON.getJSONObject("reviews");
            JSONArray reviewsArray = reviewsObject.getJSONArray("results");

            //Iterate to each movie object and store its necessary info
            for(int i = 0; i<reviewsArray.length(); i++){
                JSONObject reviewInfo = reviewsArray.getJSONObject(i);

                mReviews.add(reviewInfo.getString("author") + "~#~" + reviewInfo.getString("content"));
            }

            mReviewListAdapter.updateData(mReviews);

            //Use Picasso to load the photo url
            Picasso.with(this)
                    .load(NetworkUtils.buildImageBackdropUrl(movieDetailJSON.getString("backdrop_path")))
                    .placeholder(R.drawable.placeholder_loading_image)
                    .error(R.drawable.error_placeholder)
                    .fit()
                    .into(movieBackground);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void checkIfFavorited(){
        movieFavoritesQueryHandler.startQuery(MOVIE_FAV_QUERY, null,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{mMovieInfo.movieId},
                null);
    }

    private void addToFavorites(){

        ContentValues contentValues = new ContentValues();
        // Put the task description and selected mPriority into the ContentValues
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mMovieInfo.title);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATING, mMovieInfo.rating);
        contentValues.put(MovieContract.MovieEntry.COLUMN_YEAR, mMovieInfo.year);
        contentValues.put(MovieContract.MovieEntry.COLUMN_DESCR, mMovieInfo.descr);
        contentValues.put(MovieContract.MovieEntry.COLUMN_PHOTO_URL, mPhotoUrl);
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieInfo.movieId);

        // Insert the content values via a ContentResolver
        movieFavoritesQueryHandler.startInsert(MOVIE_FAV_INSERT, null,
                MovieContract.MovieEntry.CONTENT_URI, contentValues);

    }

    private void removeFromFavorites(){
        movieFavoritesQueryHandler.startDelete(MOVIE_FAV_DELETE, null,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{mMovieInfo.movieId});
    }

    private void setFavoriteIcon(){
        if(isFavorited) {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_filled);
        }else {
            fabFavorite.setImageResource(R.drawable.ic_action_favorite_unfilled);
        }
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {
        if(cursor == null){
            Log.i("ERROR", "NULL CURSOR");
            isFavorited = false;

        } else if(cursor.getCount() >= 1){
            Log.i("FAVORITED", "saved in db");
            while (cursor.moveToNext()){
                Log.i("ENTRY", "ID = " + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))
                        +", TITLE = " + cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)));
            }
            cursor.close();

            isFavorited = true;
        }else {

            isFavorited = false;
        }

        setFavoriteIcon();
    }

    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {
        if(uri != null) {
            Toast.makeText(getBaseContext(), uri.toString(), Toast.LENGTH_LONG).show();
            isFavorited = true;
            setFavoriteIcon();
        }
    }

    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {
        if(result != 0){
            isFavorited = false;
            setFavoriteIcon();
        }
    }
}
