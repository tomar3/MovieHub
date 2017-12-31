package com.codertal.moviehub.base.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder> {
    private List<T> items;
    private View emptyView;
    private OnViewHolderClickListener<T> listener;

    public interface OnViewHolderClickListener<T> {
        void onViewHolderClick(View view, int position, T item);
    }


    protected abstract View createView(ViewGroup viewGroup, int viewType);

    protected abstract void bindView(T item, BaseRecyclerViewAdapter.BaseViewHolder viewHolder);

    public BaseRecyclerViewAdapter(OnViewHolderClickListener<T> listener, View emptyView) {
        this.listener = listener;
        this.emptyView = emptyView;
        items = new ArrayList<>();
    }

    @Override
    public abstract BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType);

    @Override
    public void onBindViewHolder(BaseRecyclerViewAdapter.BaseViewHolder holder, int position) {
        bindView(items.get(position), holder);
    }

    @Override
    public int getItemCount() {
        if(emptyView != null) {
            if(items.isEmpty()){
                emptyView.setVisibility(View.VISIBLE);
            }else {
                emptyView.setVisibility(View.GONE);
            }
        }

        return items.size();
    }


    public void updateItems(List<T> list) {
        items = list;
        notifyDataSetChanged();
    }

    protected int getItemPosition(T item) {
        return items.indexOf(item);
    }

    public class BaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public BaseViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onViewHolderClick(view, getAdapterPosition(), items.get(getAdapterPosition()));
            }
        }
    }
}
