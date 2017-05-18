package com.tomar.udacity.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomar.udacity.popularmovies.tasks.MovieQueryTask;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieGridAdapter.GridItemClickListener,
        MovieQueryTask.OnMovieQueryExecuteListener{

    private static int NUMBER_OF_COLUMNS = 2;
    private static String TOP_RATED = "Top Rated";
    private static String POPULAR = "Popular";
    private static String SORT_TYPE = "sortType";
    private static String SCROLL_POSITION = "scrollPosition";
    static String MOVIE_INFO = "movieInfo";
    static String PHOTO_URL = "photoUrl";

    private Handler handler = new Handler();

    private MovieGridAdapter mMovieGridAdapter;
    private RecyclerView mMoviesGrid;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private GridLayoutManager mLayoutManager;
    private int mNumberOfMovies;
    private String mSortType;
    private ArrayList<String> mMovieUrls = new ArrayList<String>();
    private ArrayList<MovieInfo> mMovieInfos = new ArrayList<MovieInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNumberOfMovies = 0;

        //Obtain references to views
        mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mMoviesGrid = (RecyclerView) findViewById(R.id.rv_movies);

        //Create new grid layout manager and set it with the recycler view
        mLayoutManager = new GridLayoutManager(this, NUMBER_OF_COLUMNS);
        mMoviesGrid.setLayoutManager(mLayoutManager);

        mMoviesGrid.setHasFixedSize(true);

        //Create and set the movie grid adapter
        mMovieGridAdapter = new MovieGridAdapter(mNumberOfMovies, mMovieUrls, this, this);
        mMoviesGrid.setAdapter(mMovieGridAdapter);

        //If creating from a saved state restore sort type and scroll position
        final int scrollPosition;
        if(savedInstanceState != null){
            mSortType = savedInstanceState.getString(SORT_TYPE);
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }else{
            mSortType = POPULAR;
            scrollPosition = 0;
        }

        //Set the activity title according to the sort type
        setActivityTitle();

        //Populate grid with api query
        makeMovieSearchQuery(mSortType);

        //Restore scroll position if previously saved
        if(scrollPosition != 0){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMoviesGrid.scrollToPosition(scrollPosition);
                }
            }, 200);
        }


    }

    private void makeMovieSearchQuery(String filterType) {
        String movieBaseUrl;

        //Obtain api base url string based on sort type
        if(filterType.equals(POPULAR)){
            movieBaseUrl = NetworkUtils.getMovieSearchPopularUrl();
        }else {
            movieBaseUrl = NetworkUtils.getMovieSearchTopUrl();
        }

        //Build search url with api key
        URL movieSearchUrl = NetworkUtils.buildSearchUrl(movieBaseUrl);

        //Fire off a AsyncTask to execute the http request
        new MovieQueryTask(this).execute(movieSearchUrl);
    }

    private void displayResults(boolean success){
        //Display error view or movie grid based on success status of http request
        if(success){
            mErrorMessage.setVisibility(View.INVISIBLE);
            mMoviesGrid.setVisibility(View.VISIBLE);
        }else{
            mErrorMessage.setVisibility(View.VISIBLE);
            mMoviesGrid.setVisibility(View.INVISIBLE);
        }

    }

    private void setActivityTitle(){
        //Set title of activity based on sort type
        if(getSupportActionBar()!= null){

            if(mSortType.equals(POPULAR)){
                getSupportActionBar().setTitle(getResources().getString(R.string.main_title_pop));
            }else{
                getSupportActionBar().setTitle(getResources().getString(R.string.main_title_top));
            }

        }
    }

    private void loadMovieUrls(JSONObject movieJsonResult){

        //Load each movie's information from json object retrieved from http request
        try{
            mMovieUrls.clear();
            mMovieInfos.clear();

            JSONArray moviesArray = movieJsonResult.getJSONArray("results");
            mNumberOfMovies = moviesArray.length();

            //Iterate to each movie object and store its necessary info
            for(int i = 0; i<mNumberOfMovies; i++){
                JSONObject movieInfo = moviesArray.getJSONObject(i);

                //Store movie image url string
                String movieImageUrl = NetworkUtils.buildImageUrl(movieInfo.getString("poster_path"));
                mMovieUrls.add(movieImageUrl);

                //Store movie stats and info
                mMovieInfos.add(new MovieInfo(
                        movieInfo.getString("title"),
                        movieInfo.getString("vote_average"),
                        movieInfo.getString("release_date"),
                        movieInfo.getString("overview")
                ));

            }


            mMovieGridAdapter.updateDataSet();
            mLayoutManager.scrollToPositionWithOffset(0, 0);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save sort type and scroll position
        outState.putString(SORT_TYPE, mSortType);
        outState.putInt(SCROLL_POSITION, mLayoutManager.findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){

            //Rerun the api query based on chosen sort type option
            case R.id.action_popular:
                if(!mSortType.equals(POPULAR)){
                    mSortType = POPULAR;

                    setActivityTitle();
                    makeMovieSearchQuery(mSortType);
                }
                return true;

            case R.id.action_top_rated:
                if(!mSortType.equals(TOP_RATED)){
                    mSortType = TOP_RATED;

                    setActivityTitle();
                    makeMovieSearchQuery(mSortType);
                }
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }


    }


    @Override
    public void onGridItemClick(int position){

        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(MainActivity.this, MovieDetailActivity.class);

        MovieInfo chosenMovieInfo = mMovieInfos.get(position);

        detailIntent.putExtra(PHOTO_URL, mMovieUrls.get(position));
        detailIntent.putExtra(MOVIE_INFO, chosenMovieInfo);

        startActivity(detailIntent);
    }

    @Override
    public void onMovieQueryPreExecute(){
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onMovieQueryPostExecute(String movieSearchResults){
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        //If http request gave back results, display them
        if (movieSearchResults != null && !movieSearchResults.equals("")) {

            displayResults(true);

            try {

                //Load the movie information from the returned JSON object
                JSONObject movieJsonResult = new JSONObject(movieSearchResults);
                loadMovieUrls(movieJsonResult);

            } catch (Throwable t) {
                Log.e("MainActivity", "Bad JSON: "  + movieSearchResults );
                displayResults(false);
            }
        } else {

            //Display network error message otherwise
            displayResults(false);
        }
    }
}
