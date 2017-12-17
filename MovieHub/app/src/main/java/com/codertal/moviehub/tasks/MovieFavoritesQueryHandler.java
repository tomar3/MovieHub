package com.codertal.moviehub.tasks;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

public class MovieFavoritesQueryHandler extends AsyncQueryHandler{

    private OnMovieFavoriteQueryListener movieFavoriteQueryListener;

    public interface OnMovieFavoriteQueryListener{
        public void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor);
        public void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri);
        public void onMovieFavoriteDeleteComplete(int token, Object cookie, int result);
    }

    public MovieFavoritesQueryHandler(ContentResolver cr, OnMovieFavoriteQueryListener movieFavoriteQueryListener) {
        super(cr);
        this.movieFavoriteQueryListener = movieFavoriteQueryListener;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        movieFavoriteQueryListener.onMovieFavoriteQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        movieFavoriteQueryListener.onMovieFavoriteInsertComplete(token, cookie, uri);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        movieFavoriteQueryListener.onMovieFavoriteDeleteComplete(token, cookie, result);
    }

}
