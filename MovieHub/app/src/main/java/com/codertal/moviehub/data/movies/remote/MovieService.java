package com.codertal.moviehub.data.movies.remote;

import com.codertal.moviehub.data.movies.MoviesResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class MovieService {

    private static final String MOVIE_SEARCH_BASE_URL = "http://api.themoviedb.org/3/movie/";

    public interface API {
        @GET("popular?")
        Single<MoviesResponse> getPopularMovies(@Query("api_key") String apiKey);

        @GET("top_rated?")
        Single<MoviesResponse> getTopRatedMovies(@Query("api_key") String apiKey);
    }

    public static MovieService.API getMovieService(){
        return RetrofitMovieClient.getClient(MOVIE_SEARCH_BASE_URL).create(API.class);
    }
}
