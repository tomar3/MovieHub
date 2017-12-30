package com.codertal.moviehub.data.movies.local;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Handler;
import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesContentObserver;
import com.codertal.moviehub.data.movies.local.task.MovieFavoritesQueryHandler;
import com.codertal.moviehub.features.movies.favorites.FavoriteMoviesObserver;

public class LocalMovieService {

    private static final int MOVIE_FAV_QUERY = 20;
    private static final int MOVIE_FAV_INSERT = 9;
    private static final int MOVIE_FAV_DELETE = 8;

    private final ContentResolver mContentResolver;
    private MovieFavoritesQueryHandler mMovieFavoritesQueryHandler;
    private FavoriteMoviesContentObserver mFavoritesContentObserver;

    public LocalMovieService(ContentResolver contentResolver) {
        mContentResolver = contentResolver;
        mMovieFavoritesQueryHandler = new MovieFavoritesQueryHandler(mContentResolver);
        mFavoritesContentObserver = new FavoriteMoviesContentObserver(new Handler());
    }

    public void getFavoriteMovies(FavoriteMoviesObserver favoriteMoviesObserver) {
        unregisterFavoritesObserver();

        //Initialize components for communicating with content provider
        mMovieFavoritesQueryHandler.setMovieFavoriteQueryListener(favoriteMoviesObserver);
        mFavoritesContentObserver.setFavoritesChangeObserver(favoriteMoviesObserver);

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

    public void addMovieToFavorites(@NonNull Movie movie,
                                    MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener movieFavoriteQueryListener) {

        mMovieFavoritesQueryHandler.setMovieFavoriteQueryListener(movieFavoriteQueryListener);

        ContentValues contentValues = new ContentValues();

        //Put the movie info into the ContentValues
        contentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, movie.getTitle());
        contentValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, movie.getVoteAverage());
        contentValues.put(MovieContract.MovieEntry.COLUMN_YEAR, movie.getReleaseDate());
        contentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, movie.getOverview());
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getPosterPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_BACKDROP_PATH, movie.getBackdropPath());
        contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movie.getId().toString());

        //Insert the content values via a ContentResolver
        mMovieFavoritesQueryHandler.startInsert(MOVIE_FAV_INSERT, null,
                MovieContract.MovieEntry.CONTENT_URI, contentValues);
    }

    public void removeMovieFromFavorites(@NonNull String movieId,
                                         MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener movieFavoriteQueryListener) {

        mMovieFavoritesQueryHandler.setMovieFavoriteQueryListener(movieFavoriteQueryListener);

        mMovieFavoritesQueryHandler.startDelete(MOVIE_FAV_DELETE, null,
                MovieContract.MovieEntry.CONTENT_URI,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movieId});
    }

    public void checkIfMovieFavorited(@NonNull String movieId,
                                      MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener movieFavoriteQueryListener) {

        mMovieFavoritesQueryHandler.setMovieFavoriteQueryListener(movieFavoriteQueryListener);

        mMovieFavoritesQueryHandler.startQuery(MOVIE_FAV_QUERY, null,
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=?",
                new String[]{movieId},
                null);
    }

    public void unregisterFavoritesObserver() {
        mContentResolver.unregisterContentObserver(mFavoritesContentObserver);
    }
}
