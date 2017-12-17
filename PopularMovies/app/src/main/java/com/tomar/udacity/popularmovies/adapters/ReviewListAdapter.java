package com.tomar.udacity.popularmovies.adapters;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tomar.udacity.popularmovies.R;
import com.tomar.udacity.popularmovies.model.Review;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder>{

    private int mNumberOfItems;
    private ArrayList<Review> mReviews;
    private TextView mEmptyReviewsView;
    private ArrayList<Integer> mExpandedViewPositions;

    public ReviewListAdapter(ArrayList<Review> reviews, TextView emptyReviewsView,
                             ArrayList<Integer> expandedViewPositions){

        mNumberOfItems = reviews.size();
        mExpandedViewPositions = expandedViewPositions;
        mReviews = reviews;
        mEmptyReviewsView = emptyReviewsView;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        //Inflate the review_list_item layout xml
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.review_list_item, viewGroup, false);

        return new ReviewViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ReviewViewHolder holder, int position) {
        final int itemPosition = position;
        String author = mReviews.get(position).author;
        String content = mReviews.get(position).content;

        holder.mReviewAuthor.setText(author);
        holder.mReviewContent.setText(content);

        holder.mReviewContent.post(new Runnable() {
            @Override
            public void run() {

                //Hide the expand arrow if the text view is short
                if(holder.mReviewContent.getLineCount() <= holder.mReviewContent.getMaxLines()){
                    holder.mExpandArrow.setVisibility(View.INVISIBLE);
                }

                //Restore expanded state if previously saved
                if(mExpandedViewPositions.contains(itemPosition)){
                    holder.expandTextView(true);
                }
            }
        });



    }

    @Override
    public int getItemCount(){
        //Display empty view if no reviews exist
        if(mNumberOfItems > 0){
            mEmptyReviewsView.setVisibility(View.GONE);
        }else {
            mEmptyReviewsView.setVisibility(View.VISIBLE);
        }

        return mNumberOfItems;
    }

    public void updateData(ArrayList<Review> reviews){
        mReviews = reviews;
        mNumberOfItems = reviews.size();
        this.notifyDataSetChanged();
    }

    public ArrayList<Integer> getExpandedViewPositions(){
        return mExpandedViewPositions;
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
            expandTextView(false);
        }

        private void expandTextView(boolean isRestoring){
            int maxLines = 3;
            int expandToLines = (mReviewContent.getMaxLines() == maxLines) ? mReviewContent.getLineCount(): maxLines;

            //Update list of expanded view positions if not coming from a restore
            if(!isRestoring){
                if(expandToLines > maxLines){
                    mExpandedViewPositions.add(getAdapterPosition());
                }else{
                    if(mExpandedViewPositions.contains(getAdapterPosition())){
                        mExpandedViewPositions.remove((Integer)getAdapterPosition());
                    }
                }
            }


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
