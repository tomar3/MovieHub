package com.codertal.moviehub.features.moviedetail;

import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.videos.model.Video;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailPresenter extends MovieDetailContract.Presenter {

    @NonNull
    private MovieDetailContract.View mMovieDetailView;

    private String mMovieId;
    private MovieRepository mMovieRepository;

    public MovieDetailPresenter(@NonNull MovieDetailContract.View mMovieDetailView, String mMovieId,
                                MovieRepository mMovieRepository) {
        this.mMovieDetailView = mMovieDetailView;
        this.mMovieId = mMovieId;
        this.mMovieRepository = mMovieRepository;
    }

    @Override
    void loadMovieDetails() {
         mCompositeDisposable.add(mMovieRepository.getMovieDetails(mMovieId)
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribeWith(new DisposableSingleObserver<MovieDetailResponse>() {
                     @Override
                     public void onSuccess(MovieDetailResponse movieDetailResponse) {
                         List<Video> videos = movieDetailResponse.getVideos().getResults();
                         List<Review> reviews = movieDetailResponse.getReviews().getResults();

                         mMovieDetailView.displayVideos(videos);
                         mMovieDetailView.displayReviews(reviews);
                     }

                     @Override
                     public void onError(Throwable e) {
                         Timber.e(e);
                     }
                 }));
    }
}
