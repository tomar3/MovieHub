package com.codertal.moviehub.di;

import com.codertal.moviehub.data.movies.remote.MovieService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Inject application-wide dependencies.
 */
@Module
public class AppModule {

    @Provides
    @Singleton
    MovieService.API provideMoveServiceApi() {
        return MovieService.getMovieService();
    }

}