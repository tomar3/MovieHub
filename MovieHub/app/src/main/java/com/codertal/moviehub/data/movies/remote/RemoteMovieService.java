package com.codertal.moviehub.data.movies.remote;

import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.movies.model.MoviesResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public class RemoteMovieService {

    private static final String MOVIE_SEARCH_BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String VIDEOS_AND_REVIEWS = "videos,reviews";

    public interface API {
        @GET("popular?")
        Single<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

        @GET("top_rated?")
        Single<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);

        @GET("{movie_id}?")
        Single<MovieDetailResponse> getMovieDetails(@Path("movie_id") String movieId,
                                                    @Query("api_key") String apiKey,
                                                    @Query("append_to_response") String appendToResponse);
    }

    public static RemoteMovieService.API getMovieService(){
        return RetrofitMovieClient.getClient(MOVIE_SEARCH_BASE_URL).create(API.class);
    }
}
