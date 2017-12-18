package com.codertal.moviehub.features.movies;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;

public class MoviesPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MoviesContract.View moviesView;

    private MoviesPresenter moviesPresenter;


    @Before
    public void setUp(){
        moviesPresenter = new MoviesPresenter();
    }

    @Test
    public void loadMovies_WhenNetworkReturnsMovies_ShouldDisplayMovies() {
        moviesPresenter.loadMovies();

        verify(moviesView).displayMovies(anyList());
    }

}
