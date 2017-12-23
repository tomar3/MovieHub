package com.codertal.moviehub.base.presenter;

import com.codertal.moviehub.base.BaseState;

public interface StatefulPresenter<S extends BaseState> {

    void restoreState(S state);

    S getState();
}
