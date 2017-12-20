package com.codertal.moviehub.tasks;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class MovieFavoritesQueryHandler extends AsyncQueryHandler{

    private OnMovieFavoriteQueryListener mMovieFavoriteQueryListener;

    public interface OnMovieFavoriteQueryListener{
        void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor);
        void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri);
        void onMovieFavoriteDeleteComplete(int token, Object cookie, int result);
    }

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
