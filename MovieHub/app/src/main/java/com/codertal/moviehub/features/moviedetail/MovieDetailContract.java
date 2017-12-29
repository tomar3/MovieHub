package com.codertal.moviehub.features.moviedetail;

import android.support.annotation.NonNull;

import com.codertal.moviehub.base.presenter.BaseRxPresenter;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.videos.model.Video;

import java.util.List;

public interface MovieDetailContract {

    interface View {

        void displayVideos(@NonNull List<Video> videos);
        void displayReviews(@NonNull List<Review> reviews);

    }


    abstract class Presenter extends BaseRxPresenter {

        abstract void loadMovieDetails();

    }

}
