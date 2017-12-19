package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.MoviesResponse;
import com.codertal.moviehub.data.movies.remote.MovieService;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.Arrays;
import java.util.Collections;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoviesPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MoviesContract.View moviesView;

    @Mock
    private MovieRepository movieRepository;

    private MoviesPresenter moviesPresenter;
    private MoviesResponse MANY_MOVIES, EMPTY_MOVIES;


    @Before
    public void setUp() {
        moviesPresenter = new MoviesPresenter(moviesView, movieRepository);

        MANY_MOVIES = new MoviesResponse();
        MANY_MOVIES.setResults(Arrays.asList(new Movie(), new Movie(), new Movie()));

        EMPTY_MOVIES = new MoviesResponse();
        EMPTY_MOVIES.setResults(Collections.emptyList());

        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void loadMovies_WhenNetworkReturnsMovies_ShouldDisplayMovies() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.loadMovies();

        verify(moviesView).displayMovies(MANY_MOVIES.getResults());
    }

    @Test
    public void loadMovies_WhenNetworkReturnsNoMovies_ShouldDisplayEmptyMovies() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(EMPTY_MOVIES));

        moviesPresenter.loadMovies();

        verify(moviesView).displayEmptyMovies();
    }

    @Test
    public void loadMovies_WhenNetworkError_ShouldDisplayLoadingError() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.error(new Throwable("error")));

        moviesPresenter.loadMovies();

        verify(moviesView).displayLoadingError();
    }

}
