package com.codertal.moviehub.data.modules;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.local.LocalMovieService;
import com.codertal.moviehub.data.movies.remote.RemoteMovieService;

import dagger.Module;
import dagger.Provides;

@Module
public class MoviesModule {

    @Provides
    MovieRepository provideMovieRepository(RemoteMovieService.API remoteMovieService, LocalMovieService localMovieService){
        return new MovieRepository(remoteMovieService, localMovieService);
    }
}
