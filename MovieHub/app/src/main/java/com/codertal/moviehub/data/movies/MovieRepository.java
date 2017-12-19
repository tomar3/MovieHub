package com.codertal.moviehub.data.movies;

import com.codertal.moviehub.BuildConfig;
import com.codertal.moviehub.data.movies.remote.MovieService;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieRepository {

    private final MovieService.API mMovieService;

    public MovieRepository(MovieService.API movieService) {
        mMovieService = movieService;
    }

    public void getPopularMovies(DisposableSingleObserver<List<MovieGson>> moviesObserver) {
        mMovieService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MoviesResponse>() {
                    @Override
                    public void onSuccess(MoviesResponse moviesResponse) {
                        moviesObserver.onSuccess(moviesResponse.getResults());
                    }

                    @Override
                    public void onError(Throwable e) {
                        moviesObserver.onError(e);
                    }
                });
    }
}
