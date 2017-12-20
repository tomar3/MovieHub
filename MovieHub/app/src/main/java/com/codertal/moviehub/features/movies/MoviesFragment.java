package com.codertal.moviehub.features.movies;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
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

import com.codertal.moviehub.MovieDetailActivity;
import com.codertal.moviehub.MovieFavoritesContentObserver;
import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.R;
import com.codertal.moviehub.adapters.MovieGridAdapter;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.local.MovieContract;
import com.codertal.moviehub.recievers.NetworkChangeBroadcastReceiver;
import com.codertal.moviehub.tasks.MovieFavoritesQueryHandler;
import com.codertal.moviehub.tasks.MoviesQueryLoader;
import com.codertal.moviehub.utilities.QueryParseUtils;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

import static com.codertal.moviehub.features.movies.MoviesFilterType.FAVORITES;

public class MoviesFragment extends Fragment implements MoviesContract.View,
        MovieGridAdapter.OnMovieClickListener,
        LoaderManager.LoaderCallbacks<String>,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener,
        MovieFavoritesContentObserver.OnFavoritesChangeObserver,
        NetworkChangeBroadcastReceiver.OnNetworkConnectedListener{

    @BindView(R.id.tv_empty_view) TextView mErrorMessage;
    @BindView(R.id.empty_favorites_view) View mEmptyFavoritesMessage;
    @BindView(R.id.pb_loading_indicator) ProgressBar mLoadingIndicator;
    @BindView(R.id.rv_movies) RecyclerView mMoviesRecycler;

    @Inject
    MovieRepository mMovieRepository;

    private static final int MOVIE_FAV_QUERY = 20;
    public static final int MOVIES_POPULAR_SEARCH_LOADER = 10;
    public static final int MOVIES_TOP_SEARCH_LOADER = 11;

    public static final String MOVIES_SEARCH_URL_KEY = "movieSearchUrlKey";
    public static final String SORT_TYPE = "sortType";

    private static final String SCROLL_POSITION = "scrollPosition";
    public static final String MOVIE_INFO = "movieInfo";

    private static final String NETWORK_ERROR = "NETWORK_ERROR";
    private static final String EMPTY_FAVORITES = "EMPTY_FAVORITES";
    private static final String EMPTY_MOVIES = "EMPTY_MOVIES";
    private static final String NO_ERROR = "NO_ERROR";

    private Handler handler = new Handler();

    private MovieGridAdapter mMovieGridAdapter;

    private String mFilterType;
    private GridLayoutManager mLayoutManager;

    private MovieFavoritesQueryHandler movieFavoritesQueryHandler;
    private MovieFavoritesContentObserver mFavoritesContentObserver;
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;

    private MoviesContract.Presenter mPresenter;

    public MoviesFragment() {}

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new MoviesPresenter(this, mMovieRepository);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        mLoadingIndicator.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        mErrorMessage.setText(getString(R.string.network_error));

        setUpMoviesRecycler();

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
                    mMoviesRecycler.scrollToPosition(scrollPosition);
                }
            }, 200);
        }

        if(!mFilterType.equals(FAVORITES)){
            mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
        }

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
        //TODO: Uncomment when implemented favorites tab
//        if(mFilterType.equals(FAVORITES)){
//            getContext().getContentResolver().unregisterContentObserver(mFavoritesContentObserver);
//        }
    }

    @Override
    public void displayEmptyMovies() {
        mErrorMessage.setText(getString(R.string.empty_movies));
        displayResults(false, EMPTY_MOVIES);
    }

    @Override
    public void displayMovies(List<Movie> movies) {
        mMovieGridAdapter.updateData(movies);
        displayResults(true, NO_ERROR);
    }

    @Override
    public void displayLoadingError() {
        mErrorMessage.setText(getString(R.string.network_error));
        displayResults(false, NETWORK_ERROR);
    }

    private void makeMovieSearchQuery(String filterType) {

//        if(filterType.equals(FAVORITES)){
//            //Initialize components for communicating with content provider
//            movieFavoritesQueryHandler = new MovieFavoritesQueryHandler(getContext().getContentResolver(), this);
//            mFavoritesContentObserver = new MovieFavoritesContentObserver(new Handler(), this);
//
//            getContext().getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
//                    false,
//                    mFavoritesContentObserver);
//            queryFavoritesContent();
//        }else{
//
//            String movieBaseUrl;
//            final int MOVIES_SEARCH_LOADER;
//
//            //Obtain api base url string based on sort type
//            if(filterType.equals(POPULAR)){
//                movieBaseUrl = NetworkUtils.getMovieSearchPopularUrl();
//                MOVIES_SEARCH_LOADER = MOVIES_POPULAR_SEARCH_LOADER;
//            }else{
//                movieBaseUrl = NetworkUtils.getMovieSearchTopUrl();
//                MOVIES_SEARCH_LOADER = MOVIES_TOP_SEARCH_LOADER;
//            }
//
//            //Build search url with api key
//            String movieSearchUrl = NetworkUtils.buildSearchUrl(movieBaseUrl);
//            Bundle urlBundle = new Bundle();
//            urlBundle.putString(MOVIES_SEARCH_URL_KEY, movieSearchUrl);
//
//            //Check if connected to internet before attempting to load
//            if(NetworkUtils.isNetworkAvailable(getContext())) {
//                getActivity().getSupportLoaderManager().restartLoader(MOVIES_SEARCH_LOADER, urlBundle,
//                        MoviesFragment.this);
//            }else {
//                displayResults(false, NETWORK_ERROR);
//            }
//        }

        showLoadingIndicator(true);
        mPresenter.loadMovies(mFilterType);
    }

    private void displayResults(boolean success, String errorType){
        //Display error view or movie grid based on success status of http request or network check
        showLoadingIndicator(false);
        if(success){
            mErrorMessage.setVisibility(View.INVISIBLE);
            mEmptyFavoritesMessage.setVisibility(View.INVISIBLE);
            mMoviesRecycler.setVisibility(View.VISIBLE);
        }else{

            if(errorType.equals(NETWORK_ERROR) || errorType.equals(EMPTY_MOVIES)){
                mErrorMessage.setVisibility(View.VISIBLE);
                mEmptyFavoritesMessage.setVisibility(View.INVISIBLE);

            }else if(errorType.equals(EMPTY_FAVORITES)) {
                mEmptyFavoritesMessage.setVisibility(View.VISIBLE);
                mErrorMessage.setVisibility(View.INVISIBLE);
            }

            mMoviesRecycler.setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save scroll position
        outState.putInt(SCROLL_POSITION, mLayoutManager.findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onMovieClick(Movie movie){
        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);

        detailIntent.putExtra(MOVIE_INFO, Parcels.wrap(movie));

        startActivity(detailIntent);

    }


    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {

        //mMovies.clear();

        //Parse favorites query result and display results if successful
//        if(QueryParseUtils.parseMovieFavoriteQuery(cursor, mMovies)){
//            displayResults(true, NO_ERROR);
//        }else {
//            displayResults(false, EMPTY_FAVORITES);
//        }

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
               // mMovies.clear();

//                //Parse, if success update adapter data set
//                if (QueryParseUtils.parseMoviesQuery(movieJsonResult, mMovies)){
//                    mMovieGridAdapter.updateDataSet();
//                }

            } catch (Throwable t) {
                Log.e("MoviesFragment", "Bad JSON: "  + movieSearchResults );
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

    private void setUpMoviesRecycler() {
        int numberOfColumns;
        numberOfColumns = getContext().getResources().getInteger(R.integer.num_of_columns);

        //Increase number of columns for landscape mode
        if(getContext().getResources().getBoolean(R.bool.is_landscape)){
            numberOfColumns ++;
        }

        //Create new grid layout manager and set it with the recycler view
        mLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        mMoviesRecycler.setLayoutManager(mLayoutManager);

        mMoviesRecycler.setHasFixedSize(true);

        //Create and set the movie grid adapter
        mMovieGridAdapter = new MovieGridAdapter(new ArrayList<>(), getContext(), this);
        mMoviesRecycler.setAdapter(mMovieGridAdapter);
    }
}
