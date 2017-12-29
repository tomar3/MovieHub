package com.codertal.moviehub.data.movies.model;


import com.codertal.moviehub.data.reviews.model.ReviewsResponse;
import com.codertal.moviehub.data.videos.model.VideosResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MovieDetailResponse {

    @SerializedName("videos")
    @Expose
    private VideosResponse videos;

    @SerializedName("reviews")
    @Expose
    private ReviewsResponse reviews;

    public VideosResponse getVideos() {
        return videos;
    }

    public void setVideos(VideosResponse videos) {
        this.videos = videos;
    }

    public ReviewsResponse getReviews() {
        return reviews;
    }

    public void setReviews(ReviewsResponse reviews) {
        this.reviews = reviews;
    }

}