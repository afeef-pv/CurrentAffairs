package com.direction.currentaffairs.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.Models.News;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Views.QuizViewPager;

import java.util.ArrayList;

public class AdapterNews extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final int NEWSCELL = 0;
    public static final int QUIZCELL = 1;
    public static final int EMPTYCELL = 2;

    private LayoutInflater inflater;
    private ArrayList<News> items;
    private Context context;
    private OnTouchCell cellTouchListener;
    private DateChangeListener dateChangeListener;

    public AdapterNews(Context context, ArrayList<News> items){
        inflater = LayoutInflater.from(context);
        this.context = context;
        update(items);
    }

    public void update(ArrayList<News> results){

        this.items = results;
        notifyDataSetChanged();
    }


    public int getItemViewType(int position){
        if(items.get(position).getHeading().equals("quiz")) return QUIZCELL;
        else if(items.get(position).getHeading().equals("none")) return EMPTYCELL;
        else return NEWSCELL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == QUIZCELL){
            View view = inflater.inflate(R.layout.daily_quiz_cell,parent,false);
            return new QuizHolder(view);
        }
        else if(viewType == EMPTYCELL){
            View view = inflater.inflate((R.layout.empty_cell),parent,false);
            return new EmptyHolder(view);
        }
        else{
            View view = inflater.inflate(R.layout.rv_cell,parent,false);
            return new AdapterNews.ItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemHolder){
            AdapterNews.ItemHolder itemHolder = (AdapterNews.ItemHolder) holder;
            News news = items.get(position);
            itemHolder.initViews(news);
        }else if(holder instanceof QuizHolder){

        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);

        if(getItemViewType(holder.getAdapterPosition()) == QUIZCELL){
            QuizHolder quizHolder = (QuizHolder) holder;
            quizHolder.setDate(items.get(holder.getAdapterPosition()).getSubpoints().get(0));
        }else if(getItemViewType(holder.getAdapterPosition()) == NEWSCELL){
            ItemHolder itemHolder = (ItemHolder) holder;
            dateChangeListener.onDateChanged(itemHolder.news.getDate());
            if(itemHolder.subpointLen > 1) {
                itemHolder.subpointNav.setVisibility(View.VISIBLE);
                itemHolder.subpointsView.setCurrentItem(0);
            }
            else itemHolder.subpointNav.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }


    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView headTextView;
        TextView subTextView;
        TextView dateView;
        ImageView newsImageView, whatsappBtn, prvsBtn, nxtBtn, saveBtn;
        ViewPager2 subpointsView;
        View itemView, subpointNav;
        int subpointLen;
        News news;
        /**
         * @param itemView
         */
        ItemHolder(@NonNull final View itemView) {
            super(itemView);
            headTextView = itemView.findViewById(R.id.cell_title);
            subTextView = itemView.findViewById(R.id.cell_subpoints);
            newsImageView = itemView.findViewById(R.id.news_img);
            dateView = itemView.findViewById(R.id.cell_dateTv);
            whatsappBtn = itemView.findViewById(R.id.rv_cell_whatsapp);
            whatsappBtn.setOnClickListener(this);
            saveBtn = itemView.findViewById(R.id.rv_cell_save);
            saveBtn.setOnClickListener(this);
            subpointNav = itemView.findViewById(R.id.subpoints_buttons);
            subpointsView = itemView.findViewById(R.id.subpoints_rv);
            prvsBtn = itemView.findViewById(R.id.prvs_subpoint);
            prvsBtn.setOnClickListener(this);
            nxtBtn = itemView.findViewById(R.id.next_subpoint);
            nxtBtn.setOnClickListener(this);
            this.itemView = itemView;
        }

        public void initViews(News news){
            this.news = news;
            this.headTextView.setText(news.getHeading());
            String result = "";
            ArrayList<String> results = new ArrayList<>();
            int i = 1;
            for(String s : news.getSubpoints()){
                if(s.length() > 2) {
                    result +=  "• "+ s + "\n\n";
                    if( i >= 3){
                        results.add(result);
                        result = "";
                        i = 0;
                    }
                    ++i;
                }
            }
            if(result.length() > 2) results.add(result);
            this.subpointLen = results.size();
            AdapterSubpoints adapterSubpoints = new AdapterSubpoints(context,results);
            adapterSubpoints.setListener(new OnTouchCell() {
                @Override
                public void onTouch() {
                    cellTouchListener.onTouch();
                }
            });
            this.subpointsView.setAdapter(adapterSubpoints);

            this.dateView.setText(CurrentAffaris.formatDate(news.getDate()));

            String url = news.getUrl();
            if(url == null || url.equals("")){
                url = "https://firebasestorage.googleapis.com/v0/b/" +
                        "current-affairs-b6a0d.appspot.com/o" +
                        "/News%20images%2Fdownload.png?alt" +
                        "=media&token=eb176519-e1c8-4bb3-9e2a-1a8cdad5527e";
                Glide.with(context)
                        .load(url)
                        .placeholder(R.drawable.download)
                        .into(this.newsImageView);
            }else {
                Glide.with(context)
                        .load(url)
                        .into(this.newsImageView);
            }
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.rv_cell_whatsapp){
                Bitmap screenShot = CurrentAffaris.getScreenShot(itemView);
                CurrentAffaris.shareWhatsapp(context, screenShot, headTextView.getText().toString());
            }

            else if(v.getId() == R.id.next_subpoint) {
                int currentIndex = subpointsView.getCurrentItem();
                if(currentIndex+1 < subpointLen) {
                    subpointsView.setCurrentItem(currentIndex + 1,true);
                }
            }

            else if(v.getId() == R.id.prvs_subpoint) {
                int currentIndex = subpointsView.getCurrentItem();
                if(currentIndex >= 0){
                    subpointsView.setCurrentItem(currentIndex - 1,true);
                }
            }

            else if(v.getId() == R.id.rv_cell_save){
                if(this.news != null)
                CurrentAffaris.save(context,this.news);
            }

            else {
//                cellTouchListener.onTouch();
            }
        }
    }

    public class EmptyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        EmptyHolder(final View itemView){
            super(itemView);
            this.itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            cellTouchListener.onTouch();
        }
    }

    private class QuizHolder extends RecyclerView.ViewHolder{
        TextView dateView;
        Button startBtn;

        public QuizHolder(@NonNull final View itemView) {
            super(itemView);
            dateView = itemView.findViewById(R.id.dqcell_tv_date);
            startBtn = itemView.findViewById(R.id.dqcell_btn_start);
        }

        public void setDate(final String date) {
            dateView.setText(formatDailyDate(date));
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuizViewPager.class);
                    //put quiz id to intent and load from database
                    intent.putExtra("id",date);
                    intent.putExtra("type", ""+CurrentAffaris.DAILY);
                    context.startActivity(intent);
                }
            });
        }
    }

    private String formatDailyDate(String date){
        String[] retDate = date.split("/");
        return retDate[0]+" "+
                CurrentAffaris.MONTHS[Integer.parseInt(retDate[1])-1]+
                " "+retDate[2];

    }

    public void setCellTouchListener(OnTouchCell myListener) {
        this.cellTouchListener = myListener;
    }

    public void setDateChangeListener(DateChangeListener dateChangeListener) {
        this.dateChangeListener = dateChangeListener;
    }

    public interface OnTouchCell{
        void onTouch();
    }

    public interface DateChangeListener{
        void onDateChanged(String date);
    }
}
