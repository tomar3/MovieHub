package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.model.Movie;

import java.util.List;

public interface MoviesContract {

    interface View {

        void displayMovies(List<Movie> movies);

    }

    interface Presenter {

        void loadMovies();

    }
}
