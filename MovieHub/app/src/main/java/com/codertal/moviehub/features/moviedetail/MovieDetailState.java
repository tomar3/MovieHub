package com.codertal.moviehub.features.moviedetail;

import java.util.ArrayList;

public class MovieDetailState implements MovieDetailContract.State {
    private final int[] scrollPositions;
    private final ArrayList<Integer> expandedViewPositions;

    public MovieDetailState(int[] scrollPositions, ArrayList<Integer> expandedViewPositions) {
        this.scrollPositions = scrollPositions;
        this.expandedViewPositions = expandedViewPositions;
    }

    @Override
    public int[] getScrollPositions() {
        return scrollPositions;
    }

    @Override
    public ArrayList<Integer> getExpandedViewPositions() {
        return expandedViewPositions;
    }
}
