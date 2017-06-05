package com.tomar.udacity.popularmovies;

import android.view.View;

public class FavoriteSnackbarListener implements View.OnClickListener {
    private OnFavoriteSnackbarClickListener mFavoriteSnackbarClickListener;

    public interface OnFavoriteSnackbarClickListener{
        public void onFavoriteSnackbarClick(View v);
    }

    public FavoriteSnackbarListener(OnFavoriteSnackbarClickListener onFavoriteSnackbarClickListener ){
        mFavoriteSnackbarClickListener = onFavoriteSnackbarClickListener;

    }

    @Override
    public void onClick(View v) {
        mFavoriteSnackbarClickListener.onFavoriteSnackbarClick(v);
    }
}
