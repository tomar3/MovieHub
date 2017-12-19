package com.codertal.moviehub.features.movies;

import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.MoviesResponse;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MoviesPresenter extends MoviesContract.Presenter {

    @NonNull
    private MoviesContract.View mMoviesView;

    private MovieRepository mMovieRepository;

    public MoviesPresenter(@NonNull MoviesContract.View moviesView,
                           @NonNull MovieRepository movieRepository) {
        mMoviesView = moviesView;
        mMovieRepository = movieRepository;
    }

    @Override
    public void loadMovies() {
        mCompositeDisposable.add(mMovieRepository.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(MoviesResponse::getResults)
                .subscribeWith(new DisposableSingleObserver<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> movies) {
                        if(movies.isEmpty()){
                            mMoviesView.displayEmptyMovies();
                        }else {
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
