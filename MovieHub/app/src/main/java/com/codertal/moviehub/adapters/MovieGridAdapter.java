package com.codertal.moviehub.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.codertal.moviehub.GlideApp;
import com.codertal.moviehub.R;
import com.codertal.moviehub.data.movies.Movie;
import com.codertal.moviehub.data.movies.MovieGson;
import com.codertal.moviehub.utilities.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {
    final private GridItemClickListener mOnItemClickListener;
    private Context mContext;
    private List<MovieGson> mMovies;

    //Declare grid item click listener for this adapter
    public interface GridItemClickListener{
        public void onGridItemClick(int position);
    }

    public MovieGridAdapter(List<MovieGson> movies, Context context,
                            GridItemClickListener gridItemClickListener){
        mMovies = movies;

        //Use main activity for the context and grid item click listener
        mContext = context;
        mOnItemClickListener = gridItemClickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //Inflate the grid_item layout xml
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.grid_item, viewGroup, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
            GlideApp.with(mContext)
                    .load(NetworkUtils.buildImageUrl(mMovies.get(position).getPosterPath()))
                    .placeholder(R.drawable.loading_image)
                    .error(R.drawable.error_placeholder)
                    .into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount(){
        return mMovies.size();
    }

    public void updateData(List<MovieGson> movies) {
        mMovies = movies;
        notifyDataSetChanged();
    }

    public void updateDataSet(){
        this.notifyDataSetChanged();
    }

    //Define the View Holder for this adapter
    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView mMoviePoster;

        public MovieViewHolder(View itemView){
            super(itemView);

            //View holder contains only one image view
            mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);

            //Set this instance as the on click listener for the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //Call the on grid item click method, sending the position clicked
            mOnItemClickListener.onGridItemClick(getAdapterPosition());
        }


    }
}
