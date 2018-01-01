package com.codertal.moviehub.features.movies.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.codertal.moviehub.GlideApp;
import com.codertal.moviehub.R;
import com.codertal.moviehub.base.adapter.BaseRecyclerViewAdapter;
import com.codertal.moviehub.data.movies.model.Movie;
import com.codertal.moviehub.utilities.NetworkUtils;

import butterknife.BindView;

public class MovieListAdapter extends BaseRecyclerViewAdapter<Movie> {

    public MovieListAdapter(OnViewHolderClickListener<Movie> onViewHolderClickListener,
                            View emptyView){
        super(onViewHolderClickListener, emptyView);
    }

    @Override
    protected View createView(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        return inflater.inflate(R.layout.list_item_movie, viewGroup, false);
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        return new MovieViewHolder(createView(viewGroup, viewType));
    }

    @Override
    protected void bindView(Movie item, BaseRecyclerViewAdapter.BaseViewHolder viewHolder) {
        MovieViewHolder holder = (MovieViewHolder) viewHolder;

        GlideApp.with(holder.mMoviePoster.getContext())
                .load(NetworkUtils.buildPosterUrl(item.getPosterPath()))
                .placeholder(R.drawable.loading_image)
                .error(R.drawable.error_placeholder)
                .into(holder.mMoviePoster);
    }

    class MovieViewHolder extends BaseViewHolder{
        @BindView(R.id.iv_movie_poster)
        ImageView mMoviePoster;

        MovieViewHolder(View itemView){
            super(itemView);
        }
    }
}
