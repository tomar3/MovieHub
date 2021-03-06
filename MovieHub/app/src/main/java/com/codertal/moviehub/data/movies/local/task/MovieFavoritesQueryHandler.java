package com.codertal.moviehub.data.movies.local.task;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class MovieFavoritesQueryHandler extends AsyncQueryHandler{

    private OnMovieFavoriteQueryListener mMovieFavoriteQueryListener;

    public MovieFavoritesQueryHandler(ContentResolver cr) {
        super(cr);
    }

    public MovieFavoritesQueryHandler(ContentResolver cr, OnMovieFavoriteQueryListener movieFavoriteQueryListener) {
        super(cr);
        this.mMovieFavoriteQueryListener = movieFavoriteQueryListener;
    }

    public void setMovieFavoriteQueryListener(OnMovieFavoriteQueryListener movieFavoriteQueryListener) {
        mMovieFavoriteQueryListener = movieFavoriteQueryListener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        mMovieFavoriteQueryListener.onMovieFavoriteQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        mMovieFavoriteQueryListener.onMovieFavoriteInsertComplete(token, cookie, uri);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        mMovieFavoriteQueryListener.onMovieFavoriteDeleteComplete(token, cookie, result);
    }

}
