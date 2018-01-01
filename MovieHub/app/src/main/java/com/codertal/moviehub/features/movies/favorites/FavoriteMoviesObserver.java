package com.codertal.moviehub.features.movies.favorites;

import com.codertal.moviehub.data.movies.local.task.OnMovieFavoriteQueryListener;

public interface FavoriteMoviesObserver extends FavoriteMoviesContentObserver.OnFavoritesChangeObserver,
        OnMovieFavoriteQueryListener {

}
