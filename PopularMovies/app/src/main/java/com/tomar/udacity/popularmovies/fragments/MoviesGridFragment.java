package com.tomar.udacity.popularmovies.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomar.udacity.popularmovies.MovieDetailActivity;
import com.tomar.udacity.popularmovies.MovieFavoritesContentObserver;
import com.tomar.udacity.popularmovies.model.Movie;
import com.tomar.udacity.popularmovies.R;
import com.tomar.udacity.popularmovies.adapters.MovieGridAdapter;
import com.tomar.udacity.popularmovies.data.MovieContract;
import com.tomar.udacity.popularmovies.recievers.NetworkChangeBroadcastReceiver;
import com.tomar.udacity.popularmovies.tasks.MovieFavoritesQueryHandler;
import com.tomar.udacity.popularmovies.tasks.MoviesQueryLoader;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;
import com.tomar.udacity.popularmovies.utilities.QueryParseUtils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoviesGridFragment extends Fragment implements MovieGridAdapter.GridItemClickListener,
        LoaderManager.LoaderCallbacks<String>,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener,
        MovieFavoritesContentObserver.OnFavoritesChangeObserver,
        NetworkChangeBroadcastReceiver.OnNetworkConnectedListener{

    @BindView(R.id.tv_empty_view) TextView mErrorMessage;
    @BindView(R.id.empty_favorites_view) View mEmptyFavoritesMessage;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_movies) RecyclerView mMoviesGrid;

    private static final int MOVIE_FAV_QUERY = 20;
    public static final int MOVIES_POPULAR_SEARCH_LOADER = 10;
    public static final int MOVIES_TOP_SEARCH_LOADER = 11;

    public static final String MOVIES_SEARCH_URL_KEY = "movieSearchUrlKey";
    public static final String TOP_RATED = "TOP RATED";
    public static final String POPULAR = "POPULAR";
    public static final String FAVORITES = "FAVORITES";
    public static final String SORT_TYPE = "sortType";

    private static final String SCROLL_POSITION = "scrollPosition";
    public static final String MOVIE_INFO = "movieInfo";

    private static final String NETWORK_ERROR = "networkError";
    private static final String EMPTY_FAVORITES = "emptyFavorites";
    private static final String NO_ERROR = "noError";

    private Handler handler = new Handler();

    private MovieGridAdapter mMovieGridAdapter;
    private ArrayList<Movie> mMovies;
    private String mFilterType;
    private GridLayoutManager mLayoutManager;

    private MovieFavoritesQueryHandler movieFavoritesQueryHandler;
    private MovieFavoritesContentObserver mFavoritesContentObserver;
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;

    public MoviesGridFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMovies = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        mLoadingIndicator.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        mErrorMessage.setText(getString(R.string.network_error));

        int numberOfColumns;
        numberOfColumns = getContext().getResources().getInteger(R.integer.num_of_columns);

        //Increase number of columns for landscape mode
        if(getContext().getResources().getBoolean(R.bool.is_landscape)){
            numberOfColumns ++;
        }

        //Create new grid layout manager and set it with the recycler view
        mLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        mMoviesGrid.setLayoutManager(mLayoutManager);

        mMoviesGrid.setHasFixedSize(true);

        //Create and set the movie grid adapter
        mMovieGridAdapter = new MovieGridAdapter(mMovies.size(), mMovies, getContext(), this);
        mMoviesGrid.setAdapter(mMovieGridAdapter);

        //If creating from a saved state retrieve scroll position
        final int scrollPosition;
        if(savedInstanceState != null){
            scrollPosition = savedInstanceState.getInt(SCROLL_POSITION);
        }else{
            scrollPosition = 0;
        }

        //Populate grid with api query depending on sort type
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

        if(!mFilterType.equals(FAVORITES)){
            mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
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
            //Initialize components for communicating with content provider
            movieFavoritesQueryHandler = new MovieFavoritesQueryHandler(getContext().getContentResolver(), this);
            mFavoritesContentObserver = new MovieFavoritesContentObserver(new Handler(), this);

            getContext().getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                    false,
                    mFavoritesContentObserver);
            queryFavoritesContent();
        }else{

            String movieBaseUrl;
            final int MOVIES_SEARCH_LOADER;

            //Obtain api base url string based on sort type
            if(filterType.equals(POPULAR)){
                movieBaseUrl = NetworkUtils.getMovieSearchPopularUrl();
                MOVIES_SEARCH_LOADER = MOVIES_POPULAR_SEARCH_LOADER;
            }else{
                movieBaseUrl = NetworkUtils.getMovieSearchTopUrl();
                MOVIES_SEARCH_LOADER = MOVIES_TOP_SEARCH_LOADER;
            }

            //Build search url with api key
            String movieSearchUrl = NetworkUtils.buildSearchUrl(movieBaseUrl);
            Bundle urlBundle = new Bundle();
            urlBundle.putString(MOVIES_SEARCH_URL_KEY, movieSearchUrl);

            //Check if connected to internet before attempting to load
            if(NetworkUtils.isNetworkAvailable(getContext())) {
                getActivity().getSupportLoaderManager().restartLoader(MOVIES_SEARCH_LOADER, urlBundle,
                        MoviesGridFragment.this);
            }else {
                displayResults(false, NETWORK_ERROR);
            }
        }
    }

    private void displayResults(boolean success, String errorType){
        //Display error view or movie grid based on success status of http request or network check
        if(success){
            mErrorMessage.setVisibility(View.INVISIBLE);
            mEmptyFavoritesMessage.setVisibility(View.INVISIBLE);
            mMoviesGrid.setVisibility(View.VISIBLE);
        }else{
            if(errorType.equals(NETWORK_ERROR)){
                mErrorMessage.setVisibility(View.VISIBLE);
                mEmptyFavoritesMessage.setVisibility(View.INVISIBLE);

            }else if(errorType.equals(EMPTY_FAVORITES)) {
                mEmptyFavoritesMessage.setVisibility(View.VISIBLE);
                mErrorMessage.setVisibility(View.INVISIBLE);
            }

            mMoviesGrid.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save scroll position
        outState.putInt(SCROLL_POSITION, mLayoutManager.findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onGridItemClick(int position){
        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);

        Movie chosenMovie = mMovies.get(position);
        detailIntent.putExtra(MOVIE_INFO, chosenMovie);

        startActivity(detailIntent);

    }


    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {

        mMovies.clear();

        //Parse favorites query result and display results if successful
        if(QueryParseUtils.parseMovieFavoriteQuery(cursor, mMovies)){
            displayResults(true, NO_ERROR);
        }else {
            displayResults(false, EMPTY_FAVORITES);
        }

        cursor.close();
        mMovieGridAdapter.updateDataSet();
        showLoadingIndicator(false);
    }

    //Implementation not needed
    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {}
    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {}

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


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        showLoadingIndicator(true);
        return new MoviesQueryLoader(getContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String movieSearchResults) {
        showLoadingIndicator(false);

        //If http request gave back results, display them
        if (movieSearchResults != null && !movieSearchResults.equals("")) {

            displayResults(true, NO_ERROR);

            try {

                //Load the movie information from the returned JSON object
                JSONObject movieJsonResult = new JSONObject(movieSearchResults);
                mMovies.clear();

                //Parse, if success update adapter data set
                if (QueryParseUtils.parseMoviesQuery(movieJsonResult, mMovies)){
                    mMovieGridAdapter.updateDataSet();
                }

            } catch (Throwable t) {
                Log.e("MoviesGridFragment", "Bad JSON: "  + movieSearchResults );
                displayResults(false, NETWORK_ERROR);
            }
        } else {

            //Display network error message otherwise
            displayResults(false, NETWORK_ERROR);
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //Not needed
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!mFilterType.equals(FAVORITES)) {
            getContext().registerReceiver(mNetworkChangeBroadcastReceiver,
                    new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(!mFilterType.equals(FAVORITES)) {
            getContext().unregisterReceiver(mNetworkChangeBroadcastReceiver);
        }
    }

    @Override
    public void onNetworkConnected() {
        makeMovieSearchQuery(mFilterType);
    }
}
