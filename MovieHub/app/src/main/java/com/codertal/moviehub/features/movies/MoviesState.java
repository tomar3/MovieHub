package com.codertal.moviehub.features.movies;

public class MoviesState implements MoviesContract.State {

    private final int lastVisibleItemPosition;

    public MoviesState(int lastVisibleItemPosition) {
        this.lastVisibleItemPosition = lastVisibleItemPosition;
    }

    @Override
    public int getLastVisibleItemPosition() {
        return lastVisibleItemPosition;
    }
}
