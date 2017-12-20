package com.codertal.moviehub.data.movies;

import com.codertal.moviehub.BuildConfig;
import com.codertal.moviehub.data.movies.local.LocalMovieService;
import com.codertal.moviehub.data.movies.remote.RemoteMovieService;
import com.codertal.moviehub.features.movies.favorites.MovieFavoritesContentObserver;
import com.codertal.moviehub.tasks.MovieFavoritesQueryHandler;

import io.reactivex.Single;

public class MovieRepository {

    private final RemoteMovieService.API mRemoteMovieService;
    private final LocalMovieService mLocalMovieService;

    public MovieRepository(RemoteMovieService.API movieService, LocalMovieService localMovieService) {
        mRemoteMovieService = movieService;
        mLocalMovieService = localMovieService;
    }

    public Single<MoviesResponse> getPopularMovies() {
        return mRemoteMovieService.getPopularMovies(BuildConfig.MOVIE_DB_API_KEY);
    }

    public Single<MoviesResponse> getTopRatedMovies() {
        return mRemoteMovieService.getTopRatedMovies(BuildConfig.MOVIE_DB_API_KEY);
    }

    public void getFavoriteMovies(MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener onMovieFavoriteQueryListener,
                                  MovieFavoritesContentObserver.OnFavoritesChangeObserver onFavoritesChangeObserver) {
        mLocalMovieService.getFavoriteMovies(onMovieFavoriteQueryListener, onFavoritesChangeObserver);
    }

    public void unregisterFavoritesObserver() {
        mLocalMovieService.unregsiterFavoritesObserver();
    }
}
