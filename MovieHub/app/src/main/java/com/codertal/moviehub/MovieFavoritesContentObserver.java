package com.codertal.moviehub;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class MovieFavoritesContentObserver extends ContentObserver {
    private OnFavoritesChangeObserver mFavoritesChangeObserver;

    public interface OnFavoritesChangeObserver{
        public void onFavoritesContentChange(Uri uri);
    }
    public MovieFavoritesContentObserver(Handler handler, OnFavoritesChangeObserver favoritesChangeObserver) {
        super(handler);
        mFavoritesChangeObserver = favoritesChangeObserver;
    }

    @Override
    public void onChange(boolean selfChange) {
        this.onChange(selfChange, null);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        mFavoritesChangeObserver.onFavoritesContentChange(uri);
    }

}
