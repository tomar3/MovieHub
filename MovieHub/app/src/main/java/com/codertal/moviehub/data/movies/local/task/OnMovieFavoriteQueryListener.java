package com.codertal.moviehub.data.movies.local.task;

import android.database.Cursor;
import android.net.Uri;

public interface OnMovieFavoriteQueryListener {

    void onMovieFavoriteQueryComplete(int token, Object cookie, Cursor cursor);
    void onMovieFavoriteInsertComplete(int token, Object cookie, Uri uri);
    void onMovieFavoriteDeleteComplete(int token, Object cookie, int result);

}
