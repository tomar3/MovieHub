package com.codertal.moviehub.features.movies;

import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;

import java.util.List;

import io.reactivex.observers.DisposableSingleObserver;

public class MoviesPresenter implements MoviesContract.Presenter {

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
        mMovieRepository.getPopularMovies(new DisposableSingleObserver<List<Movie>>() {
            @Override
            public void onSuccess(List<Movie> movies) {
                //TODO: WHY DOES THIS GET CALLED MULTIPLE TIMES
                // I THINK BECAUSE THE OTHER FRAGMENT TABS ARE CALLING THE SAME CODE
                if(movies.isEmpty()){

                }else {
                    mMoviesView.displayMovies(movies);
                }
            }

            @Override
            public void onError(Throwable e) {

            }
        });
    }
}
