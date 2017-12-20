package com.codertal.moviehub.data.movies;

import com.codertal.moviehub.BuildConfig;
import com.codertal.moviehub.data.movies.remote.MovieService;

import io.reactivex.Single;

public class MovieRepository {

    private final MovieService.API mMovieService;

    public MovieRepository(MovieService.API movieService) {
        mMovieService = movieService;
    }

    public Single<MoviesResponse> getPopularMovies() {
        return mMovieService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
    }

    public Single<MoviesResponse> getTopRatedMovies() {
        return mMovieService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
    }
}
