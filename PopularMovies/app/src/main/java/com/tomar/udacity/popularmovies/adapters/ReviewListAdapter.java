package com.tomar.udacity.popularmovies.adapters;

import android.animation.ObjectAnimator;
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

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>{

    private int mNumberOfItems;
    private ArrayList<String> mReviews;

    public ReviewListAdapter(ArrayList<String> reviews){
        mNumberOfItems = reviews.size();
        mReviews = reviews;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //Inflate the grid_item layout xml
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.review_list_item, viewGroup, false);

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        String[] reviewInfo = mReviews.get(position).split("~#~");
        String author = reviewInfo[0];
        String content = reviewInfo[1];

        holder.mReviewAuthor.setText(author);
        holder.mReviewContent.setText(content);

        holder.mReviewContent.post(new Runnable() {
            @Override
            public void run() {

                if(holder.mReviewContent.getLineCount() <= holder.mReviewContent.getMaxLines()){
                    holder.mExpandArrow.setVisibility(View.INVISIBLE);
                }
            }
        });


    }

    @Override
    public int getItemCount(){
        return mNumberOfItems;
    }

    public void updateData(ArrayList<String> reviews){
        mReviews = reviews;
        mNumberOfItems = reviews.size();
        this.notifyDataSetChanged();
    }



    //Define the View Holder for this adapter
    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mReviewAuthor, mReviewContent;
        ImageView mExpandArrow;
        int rotationAngle = 0;

        public ReviewViewHolder(View itemView){
            super(itemView);

            //View holder contains only one image view
            mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_author);
            mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
            mExpandArrow = (ImageView) itemView.findViewById(R.id.iv_expand_arrow);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int maxLines = 3;
            int expandToLines = (mReviewContent.getMaxLines() == maxLines) ? mReviewContent.getLineCount(): maxLines;

            toggleExpandArrow();
            ObjectAnimator expandAnimation = ObjectAnimator.ofInt(mReviewContent, "maxLines", expandToLines);
            expandAnimation.setDuration(200).start();
        }

        private void toggleExpandArrow(){
            rotationAngle = rotationAngle == 0 ? 180 : 0;
            mExpandArrow.animate().rotation(rotationAngle).setDuration(500).start();

        }
    }


}
