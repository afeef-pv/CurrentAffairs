package com.direction.currentaffairs.Utils;

import androidx.recyclerview.widget.RecyclerView;

public interface RecyclerViewItemOnScrollAnimator {
    public void onAnimateViewHolder(RecyclerView.ViewHolder viewHolder, int position);
    public void onPrepareToAnimateViewHolder(RecyclerView.ViewHolder viewHolder);
}