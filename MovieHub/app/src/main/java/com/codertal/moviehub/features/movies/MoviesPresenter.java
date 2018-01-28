package com.codertal.moviehub.features.movies;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.test.espresso.idling.CountingIdlingResource;

import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.model.MoviesResponse;
import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesObserver;
import com.codertal.moviehub.utilities.FavoriteMovieParser;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.codertal.moviehub.features.movies.MoviesFilterType.FAVORITES;
import static com.codertal.moviehub.features.movies.MoviesFilterType.POPULAR;
import static com.codertal.moviehub.features.movies.MoviesFilterType.TOP_RATED;

public class MoviesPresenter extends MoviesContract.Presenter implements FavoriteMoviesObserver{

    @NonNull
    private MoviesContract.View mMoviesView;

    private MovieRepository mMovieRepository;
    private MoviesContract.State mState;
    private String mFilterType;
    private boolean mMoviesDisplayed;
    private CountingIdlingResource mIdlingResource;

    public MoviesPresenter(@NonNull MoviesContract.View moviesView,
                           @NonNull MovieRepository movieRepository,
                           @NonNull String filterType,
                           CountingIdlingResource idlingResource) {
        mMoviesView = moviesView;
        mMovieRepository = movieRepository;
        mFilterType = filterType;
        mIdlingResource = idlingResource;
        mMoviesDisplayed = false;
    }

    @Override
    public void restoreState(MoviesContract.State state) {
        mState = state;
    }

    @Override
    public MoviesContract.State getState() {
        return new MoviesState(mMoviesView.getLayoutManagerPosition());
    }

    @Override
    public void unsubscribe() {
        super.unsubscribe();

        if(mFilterType.equals(FAVORITES)){
            mMovieRepository.unregisterFavoritesObserver();
        }
    }

    @Override
    public void loadMovies() {
        //mIdlingResource.increment();

        mMoviesView.displayLoadingIndicator(true);

        if(mFilterType.equals(FAVORITES)){
            loadFavoriteMovies();
        }else {

            Single<MoviesResponse> movieQuery;

            switch (mFilterType) {
                case POPULAR:
                    movieQuery = mMovieRepository.getPopularMovies();
                    break;

                case TOP_RATED:
                    movieQuery = mMovieRepository.getTopRatedMovies();
                    break;

                default:
                    throw new IllegalArgumentException("Unknown movie filter type");
            }

            mCompositeDisposable.add(movieQuery
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(MoviesResponse::getResults)
                    .subscribeWith(new DisposableSingleObserver<List<Movie>>() {
                        @Override
                        public void onSuccess(List<Movie> movies) {
                            if (movies.isEmpty()) {
                                mMoviesView.displayEmptyMovies();
                            } else {
                                //Display movies
                                mMoviesView.displayMovies(movies);

                                //Restore view state if exists
                                if(mState != null){
                                    mMoviesView.restoreLayoutManagerPosition(mState.getVisibleItemPosition());
                                }
                                mMoviesDisplayed = true;
                            }

                            //mIdlingResource.decrement();
                        }

                        @Override
                        public void onError(Throwable error) {
                            Timber.e(error);

                            mMoviesView.displayLoadingError();

                            //mIdlingResource.decrement();
                        }
                    }));
        }
    }

    @Override
    void handleNetworkConnected() {
        if(!mMoviesDisplayed){
            loadMovies();
        }
    }

    @Override
    void handleMovieClick(Movie movie) {
        mMoviesView.showMovieDetailUi(movie);
    }

    @Override
    public void onFavoritesContentChange(Uri uri) {
        mMoviesView.displayLoadingIndicator(true);
        loadFavoriteMovies();
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {

        //Parse favorites query result and display results if successful
        List<Movie> favoriteMovies = new FavoriteMovieParser(cursor).parse();
        if(favoriteMovies != null){
            mMoviesView.displayMovies(favoriteMovies);
        }else {
            mMoviesView.displayEmptyFavorites();
        }

        cursor.close();

        //mIdlingResource.decrement();
    }

    //Used for unit testing
    public void setFilterType(String filterType) {
        mFilterType = filterType;
    }

    //Implementation not needed
    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {}
    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {}

    private void loadFavoriteMovies() {
        mMovieRepository.getFavoriteMovies(this);
    }
}
