package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.base.presenter.BaseRxPresenter;
import com.codertal.moviehub.data.movies.Movie;

import java.util.List;

public interface MoviesContract {

    interface View {

        void displayEmptyMovies();
        void displayMovies(List<Movie> movies);
        void displayLoadingError();

    }

    abstract class Presenter extends BaseRxPresenter{

        abstract void loadMovies();

    }
}
