package com.tomar.udacity.popularmovies.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tomar.udacity.popularmovies.R;

import java.util.ArrayList;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder>{

    final private ListItemClickListener mOnListItemClickListener;
    private int mNumberOfItems;
    private ArrayList<String> mTrailerTitles;

    //Declare list item click listener for this adapter
    public interface ListItemClickListener{
        public void onListItemClick(int position);
    }

    public TrailerListAdapter(ArrayList<String> trailerTitles, ListItemClickListener listItemClickListener){
        mNumberOfItems = trailerTitles.size();
        mTrailerTitles = trailerTitles;

        //Use detail activity for the list item click listener
        mOnListItemClickListener = listItemClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //Inflate the grid_item layout xml
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.trailer_list_item, viewGroup, false);

        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.mTrailerTitle.setText(mTrailerTitles.get(position));
    }

    @Override
    public int getItemCount(){
        return mNumberOfItems;
    }

    public void updateData(ArrayList<String> trailerTitles){
        mTrailerTitles = trailerTitles;
        mNumberOfItems = mTrailerTitles.size();
        this.notifyDataSetChanged();
    }



    //Define the View Holder for this adapter
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTrailerTitle;

        public TrailerViewHolder(View itemView){
            super(itemView);

            //View holder contains only one image view
            mTrailerTitle = (TextView) itemView.findViewById(R.id.tv_trailer_title);

            //Set this instance as the on click listener for the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            //Call the on grid item click method, sending the position clicked
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }


    }
}
