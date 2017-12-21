package com.codertal.moviehub.data.movies.local;

import android.content.ContentResolver;
import android.os.Handler;

import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesContentObserver;
import com.codertal.moviehub.data.movies.local.task.MovieFavoritesQueryHandler;
import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesObserver;

public class LocalMovieService {

    private static final int MOVIE_FAV_QUERY = 20;

    private final ContentResolver mContentResolver;
    private MovieFavoritesQueryHandler mMovieFavoritesQueryHandler;
    private FavoriteMoviesContentObserver mFavoritesContentObserver;

    public LocalMovieService(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        mMovieFavoritesQueryHandler = new MovieFavoritesQueryHandler(mContentResolver);
        mFavoritesContentObserver = new FavoriteMoviesContentObserver(new Handler());
    }

    public void getFavoriteMovies(FavoriteMoviesObserver favoriteMoviesObserver) {

        //Initialize components for communicating with content provider
        mMovieFavoritesQueryHandler.setMovieFavoriteQueryListener(favoriteMoviesObserver);
        mFavoritesContentObserver.setFavoritesChangeObserver(favoriteMoviesObserver);

        mContentResolver.unregisterContentObserver(mFavoritesContentObserver);
        mContentResolver.registerContentObserver(MovieContract.MovieEntry.CONTENT_URI,
                false,
                mFavoritesContentObserver);

        mMovieFavoritesQueryHandler.startQuery(MOVIE_FAV_QUERY, null,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null);
    }

    public void unregsiterFavoritesObserver() {
        mContentResolver.unregisterContentObserver(mFavoritesContentObserver);
    }
}
