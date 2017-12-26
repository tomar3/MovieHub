package com.codertal.moviehub.features.movies;

public class MoviesState implements MoviesContract.State {

    private final int visibleItemPosition;

    public MoviesState(int visibleItemPosition) {
        this.visibleItemPosition = visibleItemPosition;
    }

    @Override
    public int getVisibleItemPosition() {
        return visibleItemPosition;
    }
}
