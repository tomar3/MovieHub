package com.codertal.moviehub.features.movies;

import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.model.MoviesResponse;

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

import static com.codertal.moviehub.features.movies.MoviesFilterType.FAVORITES;
import static com.codertal.moviehub.features.movies.MoviesFilterType.POPULAR;
import static com.codertal.moviehub.features.movies.MoviesFilterType.TOP_RATED;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
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
    private MoviesState MOVIES_STATE;


    @Before
    public void setUp() {
        moviesPresenter = new MoviesPresenter(moviesView, movieRepository, POPULAR);

        MANY_MOVIES = new MoviesResponse();
        MANY_MOVIES.setResults(Arrays.asList(new Movie(), new Movie(), new Movie()));

        EMPTY_MOVIES = new MoviesResponse();
        EMPTY_MOVIES.setResults(Collections.emptyList());

        MOVIES_STATE = new MoviesState(1);

        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void loadMovies_WhenPopularType_ShouldQueryPopularMovies() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.setFilterType(POPULAR);
        moviesPresenter.loadMovies();

        verify(movieRepository).getPopularMovies();
    }

    @Test
    public void loadMovies_WhenTopRatedType_ShouldQueryTopRatedMovies() {
        when(movieRepository.getTopRatedMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.setFilterType(TOP_RATED);
        moviesPresenter.loadMovies();

        verify(movieRepository).getTopRatedMovies();
    }

    @Test
    public void loadMovies_WhenFavoriteType_ShouldQueryFavoriteMovies() {

        moviesPresenter.setFilterType(FAVORITES);
        moviesPresenter.loadMovies();

        verify(movieRepository).getFavoriteMovies(any());
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
    public void loadMovies_ShouldDisplayLoadingIndicator() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.loadMovies();

        verify(moviesView).displayLoadingIndicator(true);
    }

    @Test
    public void loadMovies_WhenNetworkError_ShouldDisplayLoadingError() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.error(new Throwable("error")));

        moviesPresenter.loadMovies();

        verify(moviesView).displayLoadingError();
    }

    @Test
    public void unsubscribe_WhenFavoriteType_ShouldUnregisterFavoritesObserver() {

        moviesPresenter.setFilterType(FAVORITES);
        moviesPresenter.unsubscribe();

        verify(movieRepository).unregisterFavoritesObserver();
    }

    @Test
    public void handleNetworkConnected_WhenMoviesNotPreviouslyDisplayed_ShouldDisplayMovies() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.handleNetworkConnected();

        verify(moviesView).displayMovies(MANY_MOVIES.getResults());
    }

    @Test
    public void handleNetworkConnected_WhenMoviesAlreadyDisplayed_ShouldNotDisplayMoviesAgain() {
        when(movieRepository.getPopularMovies()).thenReturn(Single.just(MANY_MOVIES));

        moviesPresenter.loadMovies();
        moviesPresenter.handleNetworkConnected();

        verify(moviesView, times(1)).displayMovies(MANY_MOVIES.getResults());
    }

    @Test
    public void getState_ShouldGetLayoutManagerPosition() {

        moviesPresenter.getState();

        verify(moviesView).getLayoutManagerPosition();
    }
}
