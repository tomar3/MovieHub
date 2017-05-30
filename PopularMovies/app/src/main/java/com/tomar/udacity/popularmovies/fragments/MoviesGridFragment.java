package com.tomar.udacity.popularmovies.fragments;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomar.udacity.popularmovies.MainActivity;
import com.tomar.udacity.popularmovies.MovieDetailActivity;
import com.tomar.udacity.popularmovies.MovieFavoritesContentObserver;
import com.tomar.udacity.popularmovies.MovieInfo;
import com.tomar.udacity.popularmovies.R;
import com.tomar.udacity.popularmovies.adapters.MovieGridAdapter;
import com.tomar.udacity.popularmovies.data.MovieContract;
import com.tomar.udacity.popularmovies.tasks.MovieFavoritesQueryHandler;
import com.tomar.udacity.popularmovies.tasks.MovieQueryTask;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MoviesGridFragment extends Fragment implements MovieGridAdapter.GridItemClickListener,
        MovieQueryTask.OnMovieQueryExecuteListener,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener,
        MovieFavoritesContentObserver.OnFavoritesChangeObserver{

    private static int NUMBER_OF_COLUMNS;
    private static final int MOVIE_FAV_QUERY = 20;
    public static String TOP_RATED = "TOP RATED";
    public static String POPULAR = "POPULAR";
    public static String FAVORITES = "FAVORITES";
    public static String SORT_TYPE = "sortType";
    private static String SCROLL_POSITION = "scrollPosition";
    public static String MOVIE_INFO = "movieInfo";
    public static String PHOTO_URL = "photoUrl";

    private Handler handler = new Handler();

    private MovieGridAdapter mMovieGridAdapter;
    private RecyclerView mMoviesGrid;
    private TextView mErrorMessage;
    private ProgressBar mLoadingIndicator;
    private GridLayoutManager mLayoutManager;
    private int mNumberOfMovies;
    private String mFilterType;
    private ArrayList<String> mMovieUrls;
    private ArrayList<MovieInfo> mMovieInfos;
    private MovieFavoritesQueryHandler movieFavoritesQueryHandler;
    private MovieFavoritesContentObserver mFavoritesContentObserver;

    public MoviesGridFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNumberOfMovies = 0;
        mMovieUrls = new ArrayList<>();
        mMovieInfos = new ArrayList<>();
        movieFavoritesQueryHandler = new MovieFavoritesQueryHandler(getContext().getContentResolver(), this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);

        //Obtain references to views
        mErrorMessage = (TextView) rootView.findViewById(R.id.tv_error_message);
        mLoadingIndicator = (ProgressBar) rootView.findViewById(R.id.pb_loading_indicator);
        mMoviesGrid = (RecyclerView) rootView.findViewById(R.id.rv_movies);

        NUMBER_OF_COLUMNS = getContext().getResources().getInteger(R.integer.num_of_columns);


        if(getContext().getResources().getBoolean(R.bool.is_landscape)){
            NUMBER_OF_COLUMNS ++;
        }

        //Create new grid layout manager and set it with the recycler view
        mLayoutManager = new GridLayoutManager(getContext(), NUMBER_OF_COLUMNS);
        mMoviesGrid.setLayoutManager(mLayoutManager);

        mMoviesGrid.setHasFixedSize(true);

        //Create and set the movie grid adapter
        mMovieGridAdapter = new MovieGridAdapter(mNumberOfMovies, mMovieUrls, getContext(), this);
        mMoviesGrid.setAdapter(mMovieGridAdapter);

        //If creating from a saved state restore scroll position
        final int scrollPosition;
        if(savedInstanceState != null){
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }else{
            scrollPosition = 0;
        }

        //Populate grid with api query
        mFilterType = getArguments().getString(SORT_TYPE);
        makeMovieSearchQuery(mFilterType);

        //Restore scroll position if previously saved
        if(scrollPosition != 0){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMoviesGrid.scrollToPosition(scrollPosition);
                }
            }, 200);
        }

        return rootView;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mFilterType.equals(FAVORITES)){
            getContext().getContentResolver().unregisterContentObserver(mFavoritesContentObserver);
        }

    }


    private void makeMovieSearchQuery(String filterType) {

        if(filterType.equals(FAVORITES)){
            mFavoritesContentObserver = new MovieFavoritesContentObserver(new Handler(), this);
            getContext().getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                    false,
                    mFavoritesContentObserver);
            queryFavoritesContent();
        }else{
            String movieBaseUrl;

            //Obtain api base url string based on sort type
            if(filterType.equals(POPULAR)){
                movieBaseUrl = NetworkUtils.getMovieSearchPopularUrl();
            }else{
                movieBaseUrl = NetworkUtils.getMovieSearchTopUrl();
            }

            //Build search url with api key
            URL movieSearchUrl = NetworkUtils.buildSearchUrl(movieBaseUrl);

            //Fire off a AsyncTask to execute the http request
            new MovieQueryTask(this).execute(movieSearchUrl);
        }
    }

    private void displayResults(boolean success, String errorMessage){
        //Display error view or movie grid based on success status of http request
        if(success){
            mErrorMessage.setVisibility(View.INVISIBLE);
            mMoviesGrid.setVisibility(View.VISIBLE);
        }else{
            mErrorMessage.setVisibility(View.VISIBLE);
            mErrorMessage.setText(errorMessage);
            mMoviesGrid.setVisibility(View.INVISIBLE);
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
                        movieInfo.getString("overview"),
                        movieInfo.getString("id")
                ));

            }


            mMovieGridAdapter.updateDataSet();
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save sort type and scroll position
        outState.putInt(SCROLL_POSITION, mLayoutManager.findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGridItemClick(int position){

        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);

        MovieInfo chosenMovieInfo = mMovieInfos.get(position);

        detailIntent.putExtra(PHOTO_URL, mMovieUrls.get(position));
        detailIntent.putExtra(MOVIE_INFO, chosenMovieInfo);

        startActivity(detailIntent);

    }

    @Override
    public void onMovieQueryPreExecute(){
        showLoadingIndicator(true);
    }

    @Override
    public void onMovieQueryPostExecute(String movieSearchResults){
       showLoadingIndicator(false);

        //If http request gave back results, display them
        if (movieSearchResults != null && !movieSearchResults.equals("")) {

            displayResults(true, null);

            try {

                //Load the movie information from the returned JSON object
                JSONObject movieJsonResult = new JSONObject(movieSearchResults);
                loadMovieUrls(movieJsonResult);

            } catch (Throwable t) {
                Log.e("MainActivity", "Bad JSON: "  + movieSearchResults );
                displayResults(false, getString(R.string.network_error));
            }
        } else {

            //Display network error message otherwise
            displayResults(false, getString(R.string.network_error));
        }
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {
        mMovieUrls.clear();
        mMovieInfos.clear();


        if(cursor.getCount() < 1){
            displayResults(false, getString(R.string.no_favorites_added));
        }else {

            while (cursor.moveToNext()) {


                mMovieUrls.add(cursor.getString(
                        cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_PHOTO_URL)));

                //Store movie stats and info
                mMovieInfos.add(new MovieInfo(
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATING)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_YEAR)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_DESCR)),
                        cursor.getString(
                                cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID))
                ));

            }
            displayResults(true, null);

        }

        cursor.close();
        mMovieGridAdapter.updateDataSet();
        showLoadingIndicator(false);
    }

    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {
        //never used
    }

    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {
        //never used
    }

    @Override
    public void onFavoritesContentChange(Uri uri) {
        queryFavoritesContent();
    }

    private void queryFavoritesContent(){
        showLoadingIndicator(true);
        movieFavoritesQueryHandler.startQuery(MOVIE_FAV_QUERY, null,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }


    private void showLoadingIndicator(boolean isLoading){
        if(isLoading){
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }else{
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }
}
