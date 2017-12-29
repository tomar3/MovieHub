package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.base.BaseState;
import com.codertal.moviehub.base.StatefulView;
import com.codertal.moviehub.base.presenter.BaseRxPresenter;
import com.codertal.moviehub.base.presenter.StatefulPresenter;
import com.codertal.moviehub.data.movies.model.Movie;

import java.util.List;

public interface MoviesContract {

    interface View extends StatefulView<State>{

        void displayEmptyFavorites();
        void displayEmptyMovies();
        void displayMovies(List<Movie> movies);
        void displayLoadingError();
        void displayLoadingIndicator(boolean isLoading);
        int getLayoutManagerPosition();
        void restoreLayoutManagerPosition(int layoutManagerPosition);

    }

    abstract class Presenter extends BaseRxPresenter implements StatefulPresenter<State>{

        abstract void loadMovies();
        abstract void handleNetworkConnected();

    }

    interface State extends BaseState {

        int getVisibleItemPosition();

    }
}
