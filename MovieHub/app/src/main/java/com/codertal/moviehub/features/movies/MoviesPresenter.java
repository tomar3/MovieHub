package com.codertal.moviehub.features.movies;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.MoviesResponse;
import com.codertal.moviehub.features.movies.favorites.MovieFavoritesContentObserver;
import com.codertal.moviehub.tasks.MovieFavoritesQueryHandler;
import com.codertal.moviehub.utilities.QueryParseUtils;

import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

import static com.codertal.moviehub.features.movies.MoviesFilterType.FAVORITES;
import static com.codertal.moviehub.features.movies.MoviesFilterType.POPULAR;
import static com.codertal.moviehub.features.movies.MoviesFilterType.TOP_RATED;

public class MoviesPresenter extends MoviesContract.Presenter implements
        MovieFavoritesContentObserver.OnFavoritesChangeObserver,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener {

    @NonNull
    private MoviesContract.View mMoviesView;

    private MovieRepository mMovieRepository;
    private String mFilterType;

    public MoviesPresenter(@NonNull MoviesContract.View moviesView,
                           @NonNull MovieRepository movieRepository,
                           @NonNull String filterType) {
        mMoviesView = moviesView;
        mMovieRepository = movieRepository;
        mFilterType = filterType;
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
                    movieQuery = mMovieRepository.getPopularMovies();
                    break;
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
                                mMoviesView.displayMovies(movies);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            Timber.e(error);

                            mMoviesView.displayLoadingError();
                        }
                    }));
        }
    }

    private void loadFavoriteMovies() {
        mMovieRepository.getFavoriteMovies(this, this);
    }

    @Override
    public void onFavoritesContentChange(Uri uri) {
        mMoviesView.displayLoadingIndicator(true);
        loadFavoriteMovies();
    }

    @Override
    public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor) {

        //Parse favorites query result and display results if successful
        List<Movie> favoriteMovies = QueryParseUtils.parseMovieFavoriteQuery(cursor);
        if(favoriteMovies != null){
            mMoviesView.displayMovies(favoriteMovies);
        }else {
            mMoviesView.displayEmptyFavorites();
        }

        cursor.close();
    }

    //Implementation not needed
    @Override
    public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri) {}
    @Override
    public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result) {}
}
