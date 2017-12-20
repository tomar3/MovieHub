package com.codertal.moviehub.di;

import com.codertal.moviehub.features.movies.MoviesActivity;
import com.codertal.moviehub.data.modules.MoviesModule;
import com.codertal.moviehub.features.movies.MoviesFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * Binds all sub-components within the app.
 */
@Module
public abstract class BuildersModule {

    @ContributesAndroidInjector()
    abstract MoviesActivity bindMainActivity();

    @ContributesAndroidInjector(modules = MoviesModule.class)
    abstract MoviesFragment bindMoviesFragment();

}
