package com.direction.currentaffairs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.R;
import com.direction.currentaffairs.RealmModels.RealmNews;
import com.direction.currentaffairs.Views.NewsViewActivity;

import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

public class AdapterSavedNews  extends RealmRecyclerViewAdapter<RealmNews, AdapterSavedNews.ItemViewHolder>{

    private LayoutInflater inflater;
    private RealmResults<RealmNews> newses;
    private Context context;

    public AdapterSavedNews(@Nullable Context context, @Nullable RealmResults<RealmNews> data, boolean autoUpdate) {
        super((OrderedRealmCollection<RealmNews>) data, autoUpdate);
        newses = data;
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    //    public AdapterSavedNews(Context context, ArrayList<RealmNews> newses){
//        inflater = LayoutInflater.from(context);
//        this.context = context;
//        this.newses = newses;
//    }
    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_news_saved,parent,false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.title.setText(newses.get(position).getHeading());
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView title;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.nws_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, NewsViewActivity.class);
            intent.putExtra("id", newses.get(getAdapterPosition()).getId());
            context.startActivity(intent);
        }
    }
}
