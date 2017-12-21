package com.codertal.moviehub.features.movies.favorites;

import com.codertal.moviehub.data.movies.local.task.MovieFavoritesQueryHandler;

public interface FavoriteMoviesObserver extends FavoriteMoviesContentObserver.OnFavoritesChangeObserver,
        MovieFavoritesQueryHandler.OnMovieFavoriteQueryListener {

}
