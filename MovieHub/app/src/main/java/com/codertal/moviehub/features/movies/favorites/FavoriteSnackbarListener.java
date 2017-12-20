package com.codertal.moviehub.features.movies.favorites;

import android.view.View;

public class FavoriteSnackbarListener implements View.OnClickListener {
    private OnFavoriteSnackbarClickListener mFavoriteSnackbarClickListener;

    public interface OnFavoriteSnackbarClickListener{
        void onFavoriteSnackbarClick(View v);
    }

    public FavoriteSnackbarListener(OnFavoriteSnackbarClickListener onFavoriteSnackbarClickListener ){
        mFavoriteSnackbarClickListener = onFavoriteSnackbarClickListener;

    }

    @Override
    public void onClick(View v) {
        mFavoriteSnackbarClickListener.onFavoriteSnackbarClick(v);
    }
}
