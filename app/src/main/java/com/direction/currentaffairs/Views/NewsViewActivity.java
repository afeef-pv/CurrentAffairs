package com.direction.currentaffairs.Views;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.direction.currentaffairs.Adapters.AdapterNews;
import com.direction.currentaffairs.Adapters.AdapterSubpoints;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.RealmModels.RealmNews;

import java.util.ArrayList;

public class NewsViewActivity extends AppCompatActivity implements View.OnClickListener {

    TextView headTextView;
    TextView subTextView;
    TextView dateView;
    ImageView newsImageView, prvsBtn, nxtBtn;
    ViewPager2 subpointsView;
    View subpointNav;
    int subpointLen;
    RealmNews news;
    Button deletButton;

    public NewsViewActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_view);

        String id = getIntent().getExtras().getString("id");
        this.news = CurrentAffaris.read(getApplicationContext(),id);

        headTextView = this.findViewById(R.id.cell_title);
        subTextView = this.findViewById(R.id.cell_subpoints);
        newsImageView = this.findViewById(R.id.news_img);
        dateView = this.findViewById(R.id.cell_dateTv);
        subpointNav = this.findViewById(R.id.subpoints_buttons);
        subpointsView = this.findViewById(R.id.subpoints_rv);
        prvsBtn = this.findViewById(R.id.prvs_subpoint);
        prvsBtn.setOnClickListener(this);
        nxtBtn = this.findViewById(R.id.next_subpoint);
        nxtBtn.setOnClickListener(this);
        deletButton = findViewById(R.id.cell_btn_delete);
        deletButton.setOnClickListener(this);

        initViews();


    }

    public void initViews(){
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
        AdapterSubpoints adapterSubpoints = new AdapterSubpoints(this,results);
        this.subpointsView.setAdapter(adapterSubpoints);

        if(news.getDate() != null) this.dateView.setText(CurrentAffaris.formatDate(news.getDate()));

        String url = news.getUrl();
        if(url == null || url.equals("")){
            url = "https://firebasestorage.googleapis.com/v0/b/" +
                    "current-affairs-b6a0d.appspot.com/o" +
                    "/News%20images%2Fdownload.png?alt" +
                    "=media&token=eb176519-e1c8-4bb3-9e2a-1a8cdad5527e";
            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.download)
                    .into(this.newsImageView);
        }else {
            Glide.with(this)
                    .load(url)
                    .into(this.newsImageView);
        }
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.next_subpoint) {
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

        else if(v.getId() == R.id.cell_btn_delete) {
            CurrentAffaris.delete(this, news.getId(), new CurrentAffaris.OnFinishActivity() {
                @Override
                public void onFinish() {
                    finish();
                }
            });
        }

    }

    @Override
    public void finish() {
        super.finish();
    }
}
