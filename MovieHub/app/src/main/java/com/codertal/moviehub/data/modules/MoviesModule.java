package com.codertal.moviehub.data.modules;

import android.content.ContentResolver;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.local.LocalMovieService;
import com.codertal.moviehub.data.movies.remote.RemoteMovieService;

import dagger.Module;
import dagger.Provides;

@Module
public class MoviesModule {

    @Provides
    LocalMovieService provideLocalMovieService(ContentResolver cr) {
        return new LocalMovieService(cr);
    }

    @Provides
    MovieRepository provideMovieRepository(RemoteMovieService.API remoteMovieService, LocalMovieService localMovieService){
        return new MovieRepository(remoteMovieService, localMovieService);
    }
}