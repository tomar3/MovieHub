package com.codertal.moviehub.features.moviedetail.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codertal.moviehub.R;
import com.codertal.moviehub.base.adapter.BaseRecyclerViewAdapter;
import com.codertal.moviehub.data.videos.model.Video;

import butterknife.BindView;

public class TrailerListAdapter extends BaseRecyclerViewAdapter<Video>{


    public TrailerListAdapter(OnViewHolderClickListener<Video> onViewHolderClickListener,
                              View emptyView){
        super(onViewHolderClickListener, emptyView);
    }

    @Override
    protected View createView(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        return inflater.inflate(R.layout.list_item_trailer, viewGroup, false);
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        return new TrailerViewHolder(createView(viewGroup, viewType));
    }

    @Override
    protected void bindView(Video item, BaseRecyclerViewAdapter.BaseViewHolder viewHolder) {
        TrailerViewHolder holder = (TrailerViewHolder) viewHolder;

        holder.mTrailerTitle.setText(item.getName());
    }


    //Define the View Holder for this adapter
    class TrailerViewHolder extends BaseViewHolder {
        @BindView(R.id.tv_trailer_title)
        TextView mTrailerTitle;

        TrailerViewHolder(View itemView){
            super(itemView);
        }
    }
}
