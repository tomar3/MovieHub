package com.tomar.udacity.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieGridAdapter extends RecyclerView.Adapter<MovieGridAdapter.MovieViewHolder> {
    final private GridItemClickListener mOnItemClickListener;
    private int mNumberOfItems;
    private Context mContext;
    private ArrayList<String> mMovieUrls;

    //Declare grid item click listener for this adapter
    public interface GridItemClickListener{
        public void onGridItemClick(int position);
    }

    public MovieGridAdapter(int numberOfItems, ArrayList<String> movieUrls, Context context,
                            GridItemClickListener gridItemClickListener){
        mNumberOfItems = numberOfItems;
        mMovieUrls = movieUrls;

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
        //Use Picasso to load the movie poster from the url based on the position
        Picasso.with(mContext)
                .load(mMovieUrls.get(position))
                .fit()
                .into(holder.mMoviePoster);
    }

    @Override
    public int getItemCount(){
        return mNumberOfItems;
    }

    public void updateDataSet(){
        mNumberOfItems = mMovieUrls.size();
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
