package com.codertal.moviehub.features.moviedetail.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codertal.moviehub.R;
import com.codertal.moviehub.data.videos.model.Video;

import java.util.List;

public class TrailerListAdapter extends RecyclerView.Adapter<TrailerListAdapter.TrailerViewHolder>{

    final private ListItemClickListener mOnListItemClickListener;
    private int mNumberOfItems;
    private List<Video> mVideos;
    private TextView mEmptyTrailersView;

    //Declare list item click listener for this adapter
    public interface ListItemClickListener{
        void onListItemClick(int position);
    }

    public TrailerListAdapter(List<Video> videos, ListItemClickListener listItemClickListener,
                              TextView emptyTrailersView){
        mNumberOfItems = videos.size();
        mVideos = videos;
        mEmptyTrailersView = emptyTrailersView;

        //Use detail activity for the list item click listener
        mOnListItemClickListener = listItemClickListener;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //Inflate the trailer_list_item layout xml
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.trailer_list_item, viewGroup, false);

        return new TrailerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        holder.mTrailerTitle.setText(mVideos.get(position).getName());
    }

    @Override
    public int getItemCount(){
        //Show empty view if no trailers exist
        if(mNumberOfItems > 0){
            mEmptyTrailersView.setVisibility(View.GONE);
        }else {
            mEmptyTrailersView.setVisibility(View.VISIBLE);
        }
        return mNumberOfItems;
    }

    public void updateData(List<Video> trailerTitles){
        mVideos = trailerTitles;
        mNumberOfItems = mVideos.size();
        this.notifyDataSetChanged();
    }


    //Define the View Holder for this adapter
    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mTrailerTitle;

        public TrailerViewHolder(View itemView){
            super(itemView);

            mTrailerTitle = itemView.findViewById(R.id.tv_trailer_title);

            //Set this instance as the on click listener for the item view
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v){
            mOnListItemClickListener.onListItemClick(getAdapterPosition());
        }
    }
}
