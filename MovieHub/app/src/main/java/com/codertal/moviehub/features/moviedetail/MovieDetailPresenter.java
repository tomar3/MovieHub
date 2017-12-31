package com.codertal.moviehub.features.moviedetail;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.codertal.moviehub.data.movies.MovieRepository;
import com.codertal.moviehub.data.movies.local.MovieContract;
import com.codertal.moviehub.data.movies.local.task.MovieFavoritesQueryHandler;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.data.movies.model.MovieDetailResponse;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.data.videos.model.Video;
import com.codertal.moviehub.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MovieDetailPresenter extends MovieDetailContract.Presenter {

    @NonNull
    private MovieDetailContract.View mMovieDetailView;

    private Movie mMovie;
    private MovieDetailContract.State mState;
    private List<Video> mVideos;
    private MovieRepository mMovieRepository;
    private MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener mMovieFavoriteQueryListener;
    private boolean mIsFavorited, mHitSnackbar, mNetworkErrorShown;


    public MovieDetailPresenter(@NonNull MovieDetailContract.View movieDetailView, Movie movie,
                                MovieRepository movieRepository,
                                MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener movieFavoriteQueryListener) {
        mMovieDetailView = movieDetailView;
        mMovie = movie;
        mMovieRepository = movieRepository;
        mMovieFavoriteQueryListener = movieFavoriteQueryListener;

        mVideos = new ArrayList<>();
    }

    @Override
    public void restoreState(MovieDetailContract.State state) {
        mState = state;
    }

    @Override
    public MovieDetailContract.State getState() {
        return new MovieDetailState(mMovieDetailView.getScrollPositions(), mMovieDetailView.getExpandedViewPositions());
    }

    @Override
    void handleFavoriteQueryComplete(Cursor cursor) {
        //Check if results show this movie is favorited
        mIsFavorited = (cursor!=null) && (cursor.getCount() >= 1);

        if(cursor!= null){
            cursor.close();
        }

        mMovieDetailView.fillFavoriteIcon(mIsFavorited);
    }

    @Override
    void loadMovieDetails() {
        mMovieDetailView.displayMovieDetails(mMovie);
        checkIfMovieFavorited();

        mCompositeDisposable.add(mMovieRepository.getMovieDetails(mMovie.getId().toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<MovieDetailResponse>() {
                    @Override
                    public void onSuccess(MovieDetailResponse movieDetailResponse) {
                        mVideos = movieDetailResponse.getVideos().getResults();
                        List<Review> reviews = movieDetailResponse.getReviews().getResults();

                        mMovieDetailView.displayVideos(mVideos);
                        mMovieDetailView.displayReviews(reviews);

                        //Restore view state if exists
                        if(mState != null) {
                            mMovieDetailView.setExpandedViewPositions(mState.getExpandedViewPositions());
                            mMovieDetailView.scrollPage(mState.getScrollPositions()[0], mState.getScrollPositions()[1]);
                        }

                        mMovieDetailView.displayBackdrop(mMovie.getBackdropPath());

                        if(mVideos.isEmpty()){
                            mMovieDetailView.displayBackdropPlayButton(false);
                            mMovieDetailView.displayShareMenuItem(false);
                        }else{
                            mMovieDetailView.displayBackdropPlayButton(true);
                            mMovieDetailView.displayShareMenuItem(true);
                        }

                        mNetworkErrorShown = false;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e);

                        mNetworkErrorShown = true;
                        mMovieDetailView.displayNetworkError();
                    }
                }));
    }


    @Override
    void handleShareClick() {
        //Share the first trailer video if it exists
        if(!mVideos.isEmpty()){
            mMovieDetailView.showShareVideoUi(NetworkUtils.buildYouTubeUrl(mVideos.get(0).getKey()), mMovie.getTitle());

        }else{
            mMovieDetailView.displayNoVideosMessage();
        }
    }

    @Override
    void handleFavoriteClick() {
        //Remove or add the movie to favorites depending on its favorited status
        if(mIsFavorited){
            removeFromFavorites();
        }else {
            addToFavorites();
        }
    }

    @Override
    void handleMovieFavorited(Uri uri) {
        //Display favorite icon accordingly and show snackbar if not coming from an undo operation
        if(uri != null) {
            mIsFavorited = true;
            mMovieDetailView.fillFavoriteIcon(true);

            if(mHitSnackbar){
                mHitSnackbar = false;
            }else{
                mMovieDetailView.displayFavoriteSnackbar(mIsFavorited);
            }
        }
    }

    @Override
    void handleMovieUnfavorited(int result) {
        //Display favorite icon accordingly and show snackbar if not coming from an undo operation
        if(result != 0){
            mIsFavorited = false;
            mMovieDetailView.fillFavoriteIcon(false);

            if(mHitSnackbar){
                mHitSnackbar = false;
            }else{
                mMovieDetailView.displayFavoriteSnackbar(mIsFavorited);
            }
        }
    }

    @Override
    void handleFavoriteSnackbarClick() {
        mHitSnackbar = true;

        //Undo the favorite
        if(mIsFavorited){
            removeFromFavorites();

        }else{  //Undo the unfavorite
            addToFavorites();
        }
    }

    @Override
    void handleBackdropPlayButtonClick() {
        //Launch first available trailer if user clicked play button on backdrop photo
        if(!mVideos.isEmpty()){
            mMovieDetailView.showVideoUi(mVideos.get(0).getKey());
        }
    }

    @Override
    void handleVideoItemClick(Video video) {
        mMovieDetailView.showVideoUi(video.getKey());
    }

    @Override
    void handleNetworkConnected() {
        //Only restart query if coming from network error
        if(mNetworkErrorShown) {
            Timber.e("NETWORK CONNECTED, LOADING DETAILS");
            loadMovieDetails();
        }
    }

    private void checkIfMovieFavorited() {
        mMovieRepository.checkIfMovieFavorited(mMovie.getId().toString(), mMovieFavoriteQueryListener);
    }

    private void addToFavorites(){
        mMovieRepository.addMovieToFavorites(mMovie, mMovieFavoriteQueryListener);
    }

    private void removeFromFavorites(){
        mMovieRepository.removeMovieFromFavorites(mMovie.getId().toString(), mMovieFavoriteQueryListener);
    }
}
