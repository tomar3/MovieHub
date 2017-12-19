package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.data.movies.MovieGson;
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

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class MoviesPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MoviesContract.View moviesView;

    @Mock
    private MovieService.API movieService;

    private MovieRepository movieRepository;
    private MoviesPresenter moviesPresenter;
    private MoviesResponse MANY_MOVIES;


    @Before
    public void setUp() {
        movieRepository = new MovieRepository(movieService);
        moviesPresenter = new MoviesPresenter(moviesView, movieRepository);

        MANY_MOVIES = new MoviesResponse();
        MANY_MOVIES.setResults(Arrays.asList(new MovieGson(), new MovieGson(), new MovieGson()));

        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void loadMovies_WhenNetworkReturnsMovies_ShouldDisplayMovies() {
        when(movieService.getPopularMovies(anyString())).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.loadMovies();

        verify(moviesView).displayMovies(MANY_MOVIES.getResults());
    }

}
