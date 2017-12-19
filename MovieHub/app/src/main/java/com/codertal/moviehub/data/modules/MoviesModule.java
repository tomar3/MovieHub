package com.codertal.moviehub.data.modules;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.remote.MovieService;

import dagger.Module;
import dagger.Provides;

@Module
public class MoviesModule {

    @Provides
    MovieRepository provideMovieRepository(MovieService.API movieService){
        return new MovieRepository(movieService);
    }
}
