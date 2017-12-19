package com.codertal.moviehub.base.presenter;

import android.support.annotation.NonNull;

import io.reactivex.disposables.CompositeDisposable;

public abstract class BaseRxPresenter {
    @NonNull
    protected CompositeDisposable mCompositeDisposable;

    public BaseRxPresenter() {
        mCompositeDisposable = new CompositeDisposable();
    }

    public void unsubscribe(){
        mCompositeDisposable.clear();
    }
}
