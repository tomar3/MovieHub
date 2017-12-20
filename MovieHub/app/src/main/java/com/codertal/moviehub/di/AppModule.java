package com.codertal.moviehub.di;

import android.content.ContentResolver;

import com.codertal.moviehub.MainApplication;
import com.codertal.moviehub.data.movies.local.LocalMovieService;
import com.codertal.moviehub.data.movies.remote.RemoteMovieService;

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
    ContentResolver provideContentResolver(MainApplication mainApplication) {
        return mainApplication.getContentResolver();
    }

    @Provides
    @Singleton
    RemoteMovieService.API provideRemoteMovieServiceApi() {
        return RemoteMovieService.getMovieService();
    }

    @Provides
    @Singleton
    LocalMovieService provideLocalMovieService(ContentResolver cr) {
        return new LocalMovieService(cr);
    }

}