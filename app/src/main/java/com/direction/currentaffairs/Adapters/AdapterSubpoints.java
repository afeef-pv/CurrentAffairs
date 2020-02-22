package com.direction.currentaffairs.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.direction.currentaffairs.R;

import java.util.ArrayList;

public class AdapterSubpoints extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<String> subpoints;
    private LayoutInflater inflater;
    private AdapterNews.OnTouchCell listener;

    public AdapterSubpoints(Context context, ArrayList<String> subpoints){
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.subpoints = subpoints;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.subpoints, parent, false);
        return new SubpointHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SubpointHolder subpointHolder = (SubpointHolder) holder;
        subpointHolder.pointsView.setText(subpoints.get(position));
    }

    @Override
    public int getItemCount() {
        return subpoints.size();
    }

    public class SubpointHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView pointsView;

        public SubpointHolder(@NonNull View itemView) {
            super(itemView);
            pointsView = itemView.findViewById(R.id.cell_sub_points);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(listener != null) listener.onTouch();
        }
    }

    public void setListener(AdapterNews.OnTouchCell listener) {
        this.listener = listener;
    }
}
