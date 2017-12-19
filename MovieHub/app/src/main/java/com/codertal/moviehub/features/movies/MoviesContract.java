package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieGson;

import java.util.List;

public interface MoviesContract {

    interface View {

        void displayMovies(List<MovieGson> movies);

    }

    interface Presenter {

        void loadMovies();

    }
}
