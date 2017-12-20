package com.codertal.moviehub.features.movies;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codertal.moviehub.features.moviedetail.MovieDetailActivity;
import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.R;
import com.codertal.moviehub.features.movies.adapter.MovieGridAdapter;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.recievers.NetworkChangeBroadcastReceiver;

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
        NetworkChangeBroadcastReceiver.OnNetworkConnectedListener{

    @BindView(R.id.tv_empty_view)
    TextView mErrorMessage;

    @BindView(R.id.empty_favorites_view)
    View mEmptyFavoritesMessage;

    @BindView(R.id.pb_loading_indicator)
    ProgressBar mLoadingIndicator;

    @BindView(R.id.rv_movies)
    RecyclerView mMoviesRecycler;

    @Inject
    MovieRepository mMovieRepository;

    public static final String SORT_TYPE = "SORT_TYPE";

    private static final String SCROLL_POSITION = "scrollPosition";
    public static final String MOVIE_INFO = "movieInfo";

    private static final String NETWORK_ERROR = "NETWORK_ERROR";
    private static final String EMPTY_FAVORITES = "EMPTY_FAVORITES";
    private static final String EMPTY_MOVIES = "EMPTY_MOVIES";
    private static final String NO_ERROR = "NO_ERROR";

    private Handler mHandler = new Handler();

    private MovieGridAdapter mMovieGridAdapter;

    private String mFilterType;
    private GridLayoutManager mLayoutManager;

    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;

    private MoviesContract.Presenter mPresenter;

    public MoviesFragment() {}

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
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
        mPresenter = new MoviesPresenter(this, mMovieRepository, mFilterType);

        mPresenter.loadMovies();

        //Restore scroll position if previously saved
        if(scrollPosition != 0){
            mHandler.postDelayed(() -> mMoviesRecycler.scrollToPosition(scrollPosition), 200);
        }

        if(!mFilterType.equals(FAVORITES)){
            mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
        }

        return rootView;
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
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save scroll position
        outState.putInt(SCROLL_POSITION, mLayoutManager.findLastVisibleItemPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void displayEmptyFavorites() {
        displayResults(false, EMPTY_FAVORITES);
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

    @Override
    public void displayLoadingIndicator(boolean isLoading){
        if(isLoading){
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }else{
            mLoadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onMovieClick(Movie movie){
        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);

        detailIntent.putExtra(MOVIE_INFO, Parcels.wrap(movie));

        startActivity(detailIntent);

    }

    @Override
    public void onNetworkConnected() {
        makeMovieSearchQuery();
    }

    private void makeMovieSearchQuery() {

//            //Check if connected to internet before attempting to load
//            if(NetworkUtils.isNetworkAvailable(getContext())) {
//                getActivity().getSupportLoaderManager().restartLoader(MOVIES_SEARCH_LOADER, urlBundle,
//                        MoviesFragment.this);
//            }else {
//                displayResults(false, NETWORK_ERROR);
//            }
//        }
    }

    private void displayResults(boolean success, String errorType){
        //Display error view or movie grid based on success status of http request or network check
        displayLoadingIndicator(false);
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
