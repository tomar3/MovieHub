package com.codertal.moviehub.data.movies;

import android.support.annotation.NonNull;

import com.codertal.moviehub.BuildConfig;
import com.codertal.moviehub.data.movies.local.LocalMovieService;
import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.movies.model.MoviesResponse;
import com.codertal.moviehub.data.movies.remote.RemoteMovieService;
import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesObserver;

import io.reactivex.Single;

import static com.codertal.moviehub.data.movies.remote.RemoteMovieService.VIDEOS_AND_REVIEWS;

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

    public void getFavoriteMovies(@NonNull FavoriteMoviesObserver favoriteMoviesObserver) {
        mLocalMovieService.getFavoriteMovies(favoriteMoviesObserver);
    }

    public Single<MovieDetailResponse> getMovieDetails(@NonNull String movieId) {
        return mRemoteMovieService.getMovieDetails(movieId, BuildConfig.MOVIE_DB_API_KEY, VIDEOS_AND_REVIEWS);
    }

    public void unregisterFavoritesObserver() {
        mLocalMovieService.unregisterFavoritesObserver();
    }
}
