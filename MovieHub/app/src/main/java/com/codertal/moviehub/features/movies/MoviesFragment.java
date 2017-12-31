package com.codertal.moviehub.features.movies;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codertal.moviehub.base.adapter.BaseRecyclerViewAdapter;
import com.codertal.moviehub.features.moviedetail.MovieDetailActivity;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.R;
import com.codertal.moviehub.features.movies.adapter.MovieGridAdapter;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.features.movies.receiver.NetworkChangeBroadcastReceiver;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.support.AndroidSupportInjection;

import static com.codertal.moviehub.features.movies.MoviesFilterType.FAVORITES;

public class MoviesFragment extends Fragment implements MoviesContract.View,
        BaseRecyclerViewAdapter.OnViewHolderClickListener<Movie>,
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
    public static final String MOVIE_INFO = "MOVIE_INFO";
    private static final String SCROLL_POSITION = "SCROLL_POSITION";

    private static final String NETWORK_ERROR = "NETWORK_ERROR";
    private static final String EMPTY_FAVORITES = "EMPTY_FAVORITES";
    private static final String EMPTY_MOVIES = "EMPTY_MOVIES";
    private static final String NO_ERROR = "NO_ERROR";

    private MoviesContract.Presenter mPresenter;

    private MovieGridAdapter mMovieGridAdapter;
    private GridLayoutManager mLayoutManager;
    private NetworkChangeBroadcastReceiver mNetworkChangeBroadcastReceiver;

    public MoviesFragment() {}

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_movies_grid, container, false);
        ButterKnife.bind(this, rootView);

        mLoadingIndicator.getIndeterminateDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        mErrorMessage.setText(getString(R.string.network_error));

        setUpMoviesRecycler();

        //Populate grid with api query depending on sort type
        String filterType = getArguments().getString(SORT_TYPE);
        mPresenter = new MoviesPresenter(this, mMovieRepository, filterType);

        mPresenter.loadMovies();

        if(!filterType.equals(FAVORITES)){
            mNetworkChangeBroadcastReceiver = new NetworkChangeBroadcastReceiver(this);
        }

        return rootView;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mNetworkChangeBroadcastReceiver != null && getContext() != null) {
            getContext().registerReceiver(mNetworkChangeBroadcastReceiver,
                    new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mNetworkChangeBroadcastReceiver != null && getContext() != null) {
            getContext().unregisterReceiver(mNetworkChangeBroadcastReceiver);
        }


        //TODO: SEE IF THIS TRULY FIXED THE CONTENT OBSERVER LEAK
//        if(mFilterType.equals(FAVORITES)){
//            mPresenter.unsubscribe();
//        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter.unsubscribe();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        writeToBundle(outState, mPresenter.getState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mPresenter.restoreState(readFromBundle(savedInstanceState));
        }
    }

    @Override
    public void writeToBundle(Bundle outState, MoviesContract.State state) {
        outState.putInt(SCROLL_POSITION, state.getVisibleItemPosition());
    }

    @Override
    public MoviesContract.State readFromBundle(@NonNull Bundle savedInstanceState) {
        int visibleItemPosition = savedInstanceState.getInt(SCROLL_POSITION);

        return new MoviesState(visibleItemPosition);
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
        mMovieGridAdapter.updateItems(movies);
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
    public void showMovieDetailUi(Movie movie) {
        //Start detail activity and pass it information about the chosen movie
        Intent detailIntent = new Intent(getContext(), MovieDetailActivity.class);

        detailIntent.putExtra(MOVIE_INFO, Parcels.wrap(movie));

        startActivity(detailIntent);
    }

    @Override
    public int getLayoutManagerPosition() {
        return mLayoutManager.findFirstVisibleItemPosition();
    }

    @Override
    public void restoreLayoutManagerPosition(int layoutManagerPosition) {
        mMoviesRecycler.postDelayed(() -> mMoviesRecycler.scrollToPosition(layoutManagerPosition), 200);
    }

    @Override
    public void onViewHolderClick(View view, int position, Movie item) {
        mPresenter.handleMovieClick(item);
    }

    @Override
    public void onNetworkConnected() {
        mPresenter.handleNetworkConnected();
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
        mMovieGridAdapter = new MovieGridAdapter(this, null);
        mMoviesRecycler.setAdapter(mMovieGridAdapter);
    }
}
