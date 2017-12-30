package com.codertal.moviehub.features.moviedetail;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.reviews.model.ReviewsResponse;
import com.codertal.moviehub.data.videos.model.Video;
import com.codertal.moviehub.data.videos.model.VideosResponse;

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

public class MovieDetailPresenterTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private MovieDetailContract.View movieDetailView;

    @Mock
    private MovieRepository movieRepository;

    private MovieDetailPresenter movieDetailPresenter;
    private String MOVIE_ID;
    private MovieDetailResponse MOVIE_DETAILS;
    private Movie MOVIE;


    @Before
    public void setUp() {
        MOVIE_ID = "1";
        MOVIE = new Movie(1, 7.8, "Title", "posterPath", "backdropPath",
                "overview", "2017");
        movieDetailPresenter = new MovieDetailPresenter(movieDetailView, MOVIE, movieRepository, null);
        MOVIE_DETAILS = new MovieDetailResponse();

        VideosResponse videosResponse = new VideosResponse();
        videosResponse.setResults(Arrays.asList(new Video(), new Video(), new Video()));

        ReviewsResponse reviewsResponse = new ReviewsResponse();
        reviewsResponse.setResults(Arrays.asList(new Review(), new Review(), new Review()));

        MOVIE_DETAILS.setVideos(videosResponse);
        MOVIE_DETAILS.setReviews(reviewsResponse);

        RxJavaPlugins.setIoSchedulerHandler(__ -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @After
    public void cleanUp() {
        RxJavaPlugins.reset();
    }

    @Test
    public void loadMovieDetails_ShouldDisplayMovieDetails() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayMovieDetails(MOVIE);
    }

    @Test
    public void loadMovieDetails_WhenNetworkReturnsVideosForMovie_ShouldDisplayVideos() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayVideos(MOVIE_DETAILS.getVideos().getResults());
    }

    @Test
    public void loadMovieDetails_WhenNetworkReturnsVideosForMovie_ShouldDisplayBackdrop() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayBackdrop(MOVIE.getBackdropPath());
    }

    @Test
    public void loadMovieDetails_WhenNetworkReturnsReviewsForMovie_ShouldDisplayReviews() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayReviews(MOVIE_DETAILS.getReviews().getResults());
    }

    @Test
    public void loadMovieDetails_WhenNetworkReturnsVideosForMovie_ShouldDisplayBackdropPlayButton() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayBackdropPlayButton(true);
    }

    @Test
    public void loadMovieDetails_WhenNetworkError_ShouldDisplayNetworkError() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.error(new Throwable("network error")));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayNetworkError();
    }

    @Test
    public void loadMovieDetails_WhenNetworkReturnsNoVideosForMovie_ShouldNotDisplayBackdropPlayButton() {
        VideosResponse emptyVideos = new VideosResponse();
        emptyVideos.setResults(Collections.emptyList());
        MOVIE_DETAILS.setVideos(emptyVideos);

        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();

        verify(movieDetailView).displayBackdropPlayButton(false);
    }

    @Test
    public void handleShareClick_WhenVideoAvailable_ShouldShowShareVideoUi() {
        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();
        movieDetailPresenter.handleShareClick();

        verify(movieDetailView).showShareVideoUi(anyString(), MOVIE.getTitle());
    }

    @Test
    public void handleShareClick_WhenNoVideoAvailable_ShouldDisplayNoVideosMessage() {
        VideosResponse emptyVideos = new VideosResponse();
        emptyVideos.setResults(Collections.emptyList());
        MOVIE_DETAILS.setVideos(emptyVideos);

        when(movieRepository.getMovieDetails(MOVIE.getId().toString())).thenReturn(Single.just(MOVIE_DETAILS));

        movieDetailPresenter.loadMovieDetails();
        movieDetailPresenter.handleShareClick();

        verify(movieDetailView).displayNoVideosMessage();
    }


}
