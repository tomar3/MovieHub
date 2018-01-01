package com.codertal.moviehub.features.moviedetail.adapter;

import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.codertal.moviehub.base.adapter.BaseRecyclerViewAdapter;
import com.codertal.moviehub.data.reviews.model.Review;
import com.codertal.moviehub.R;

import java.util.ArrayList;

import butterknife.BindView;

public class ReviewListAdapter extends BaseRecyclerViewAdapter<Review>{

    private ArrayList<Integer> mExpandedViewPositions;

    public ReviewListAdapter(OnViewHolderClickListener<Review> onViewHolderClickListener,
                             View emptyView){

        super(onViewHolderClickListener, emptyView);
        mExpandedViewPositions = new ArrayList<>();
    }

    @Override
    protected View createView(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        return inflater.inflate(R.layout.list_item_review, viewGroup, false);
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        return new ReviewViewHolder(createView(viewGroup, viewType));
    }

    @Override
    protected void bindView(Review item, BaseRecyclerViewAdapter.BaseViewHolder viewHolder) {
        ReviewViewHolder holder = (ReviewViewHolder) viewHolder;

        String author = item.getAuthor();
        String content = item.getContent();

        holder.mReviewAuthor.setText(author);
        holder.mReviewContent.setText(content);

        holder.mReviewContent.post(() -> {

            //Hide the expand arrow if the text view is short
            if(holder.mReviewContent.getLineCount() <= holder.mReviewContent.getMaxLines()){
                holder.mExpandArrow.setVisibility(View.INVISIBLE);
            }

            //Restore expanded state if previously saved
            if(mExpandedViewPositions.contains(getItemPosition(item))){
                holder.expandTextView(true);
            }
        });
    }

    public ArrayList<Integer> getExpandedViewPositions(){
        return mExpandedViewPositions;
    }

    public void setExpandedViewPositions(@NonNull ArrayList<Integer> expandedViewPositions) {
        mExpandedViewPositions = expandedViewPositions;
    }

    //Define the View Holder for this adapter
    class ReviewViewHolder extends BaseViewHolder{
        @BindView(R.id.tv_author)
        TextView mReviewAuthor;

        @BindView(R.id.tv_review_content)
        TextView mReviewContent;

        @BindView(R.id.iv_expand_arrow)
        ImageView mExpandArrow;

        int rotationAngle = 0;

        ReviewViewHolder(View itemView){
            super(itemView);
        }

        @Override
        public void onClick(View v) {
            super.onClick(v);
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
            mExpandArrow.animate().rotation(rotationAngle).setDuration(200).start();

        }
    }


}
