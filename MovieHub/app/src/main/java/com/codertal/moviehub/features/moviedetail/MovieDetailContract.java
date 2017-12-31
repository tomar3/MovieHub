package com.codertal.moviehub.features.moviedetail;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.codertal.moviehub.base.presenter.BaseRxPresenter;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.videos.model.Video;

import java.util.List;

public interface MovieDetailContract {

    interface View {

        void displayMovieDetails(@NonNull Movie movie);
        void displayVideos(@NonNull List<Video> videos);
        void displayReviews(@NonNull List<Review> reviews);
        void displayBackdrop(String backdropPath);
        void displayBackdropPlayButton(boolean display);
        void fillFavoriteIcon(boolean fill);
        void displayNetworkError();
        void displayNoVideosMessage();
        void displayFavoriteSnackbar(boolean isFavorite);
        void displayShareMenuItem(boolean display);
        void showVideoUi(@NonNull String videoKey);
        void showShareVideoUi(String videoUrl, String movieTitle);

    }


    abstract class Presenter extends BaseRxPresenter {

        abstract void loadMovieDetails();
        abstract void handleShareClick();
        abstract void handleFavoriteClick();
        abstract void handleMovieFavorited(Uri uri);
        abstract void handleMovieUnfavorited(int result);
        abstract void handleFavoriteQueryComplete(Cursor cursor);
        abstract void handleFavoriteSnackbarClick();
        abstract void handleBackdropPlayButtonClick();
        abstract void handleNetworkConnected();
        abstract void handleVideoItemClick(Video video);

    }

}
