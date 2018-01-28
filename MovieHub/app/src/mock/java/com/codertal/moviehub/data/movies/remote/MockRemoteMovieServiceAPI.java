package com.codertal.moviehub.data.movies.remote;

import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.movies.model.MoviesResponse;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.reviews.model.ReviewsResponse;
import com.codertal.moviehub.data.videos.model.Video;
import com.codertal.moviehub.data.videos.model.VideosResponse;

import java.util.Arrays;
import java.util.List;

import io.reactivex.Single;

public class MockRemoteMovieServiceAPI implements RemoteMovieService.API {

    private MoviesResponse mMoviesResponse;
    private MovieDetailResponse mMovieDetailResponse;

    public MockRemoteMovieServiceAPI() {
        mMoviesResponse = new MoviesResponse();
        mMovieDetailResponse = new MovieDetailResponse();

        mMoviesResponse.setResults(getMockMovies());

        mMovieDetailResponse.setVideos(getMockVideos());
        mMovieDetailResponse.setReviews(getMockReviews());
    }

    @Override
    public Single<MoviesResponse> getPopularMovies(String apiKey) {
        return Single.just(mMoviesResponse);
    }

    @Override
    public Single<MoviesResponse> getTopRatedMovies(String apiKey) {
        return Single.just(mMoviesResponse);
    }

    @Override
    public Single<MovieDetailResponse> getMovieDetails(String movieId, String apiKey, String appendToResponse) {
        return Single.just(mMovieDetailResponse);
    }


    private List<Movie> getMockMovies() {
        Movie movie1 = new Movie(1, 9.1, "The Movie 1", "coss7RgL0NH6g4fC2s5atvf3dFO.jpg",
                "coss7RgL0NH6g4fC2s5atvf3dFO.jpg", "This is an overview of the movie 1", "2014-09-10");
        Movie movie2 = new Movie(2, 8.1, "The Movie 2", "5vHssUeVe25bMrof1HyaPyWgaP.jpg",
                "5vHssUeVe25bMrof1HyaPyWgaP.jpg", "This is an overview of the movie 2", "2014-09-11");
        Movie movie3 = new Movie(3, 7.1, "The Movie 3", "8wBKXZNod4frLZjAKSDuAcQ2dEU.jpg",
                "8wBKXZNod4frLZjAKSDuAcQ2dEU.jpg", "This is an overview of the movie 3", "2014-09-12");
        Movie movie4 = new Movie(4, 6.1, "The Movie 4", "dfhztJRiElqmYW4kpvjYe1gENsD.jpg",
                "dfhztJRiElqmYW4kpvjYe1gENsD.jpg", "This is an overview of the movie 4", "2014-09-13");

        return Arrays.asList(movie1, movie2, movie3, movie4);
    }

    private VideosResponse getMockVideos() {
        Video video1 = new Video();
        video1.setKey("key");
        video1.setName("Official Trailer 1");

        Video video2 = new Video();
        video2.setKey("key");
        video2.setName("Official Trailer 2");

        VideosResponse videosResponse = new VideosResponse();
        videosResponse.setResults(Arrays.asList(video1, video2));

        return videosResponse;
    }

    private ReviewsResponse getMockReviews() {
        Review review1 = new Review();
        review1.setAuthor("Author 1");
        review1.setContent("Content 1");

        Review review2 = new Review();
        review2.setAuthor("Author 2");
        review2.setContent("Content 2");

        ReviewsResponse reviewsResponse = new ReviewsResponse();
        reviewsResponse.setResults(Arrays.asList(review1, review2));

        return reviewsResponse;
    }
}
