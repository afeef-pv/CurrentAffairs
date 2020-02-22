package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.direction.currentaffairs.Adapters.AdapterNews;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.Models.CurrentAffairs;
import com.direction.currentaffairs.Models.News;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Utils.DepthTransformer;
import com.direction.currentaffairs.Widgets.DatePickerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class NewsViewPager extends Fragment implements AdapterNews.DateChangeListener {
    private MainActivity.NavHideListener navHideListener;
    private ArrayList<News> newsArrayList = new ArrayList<>();
    private ViewPager2 pager;
    private CardView datePicker;
    private AdapterNews adapterNews;
    private ArrayList<CurrentAffairs> currentAffairsArrayList;
    private Calendar calendar;
    private DatePickerView datePickerView;
    private View progressBar;

    private View rootView;
    private boolean isLoading = false;
    private boolean isLoadComplete = false;


    public NewsViewPager(MainActivity.NavHideListener navHideListener) {
        this.navHideListener = navHideListener;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.fragment_read,null);
        calendar = Calendar.getInstance();
        currentAffairsArrayList = new ArrayList<>();

        datePickerView = rootView.findViewById(R.id.datePickerView);
        datePickerView.setDateChangedListener(new DatePickerView.OnDateChangedListener() {
            @Override
            public void onDateChanged(Calendar cal) {
                calendar = cal;
                String date = getStringDateDashed(calendar);
                loadData_complete(date);
            }
        });
        progressBar = rootView.findViewById(R.id.progressBar);
        datePicker = rootView.findViewById(R.id.date_card_view);


        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);
        initViewer();
        loadData(getStringDateDashed(calendar));
        return rootView;
    }

    private void initViewer(){

        adapterNews = new AdapterNews(getActivity(),newsArrayList);
        adapterNews.setDateChangeListener(this);
        adapterNews.setCellTouchListener(new AdapterNews.OnTouchCell() {
            @Override
            public void onTouch() {
                hide_unhide(false);
            }
        });
        pager = rootView.findViewById(R.id.newsViewPager);
        pager.registerOnPageChangeCallback(new OnPageChangeCallback(){
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                hide_unhide(true);
                loadLast();
            }
        });
        pager.setPageTransformer(new DepthTransformer());
        pager.setAdapter(adapterNews);
    }

    private void loadLast() {
        if(!isLoading && (newsArrayList.size() - pager.getCurrentItem()) < 4){
            calendar.add(Calendar.DAY_OF_MONTH,-1);
            loadData(getStringDateDashed(calendar));
        }
    }

    private void loadData_complete(String date) {
        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);

        isLoadComplete = true;
        currentAffairsArrayList.clear();
        newsArrayList.clear();
        loadData(date);
    }

    public void loadData(String date) {
        isLoading = true;
        date = getReverseDate(date);
        Query query = FirebaseDatabase.getInstance().getReference("currentAffairs")
                .orderByChild("date")
                .equalTo(date);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    CurrentAffairs ca = snapshot.getValue(CurrentAffairs.class);
                    currentAffairsArrayList.add(ca);
                }
                reloadAdapter();
//                datePickerView.setDate(calendar);
            } else {
                progressBar.setVisibility(View.GONE);
                CurrentAffaris.stopAnimPragress(progressBar);

                News n = new News("none");
                newsArrayList.add(n);
                adapterNews.notifyDataSetChanged();
            }
        }
        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            progressBar.setVisibility(View.GONE);
        }
    };

    public void reloadAdapter(){
        for(CurrentAffairs ca : currentAffairsArrayList) {
            for (News n : ca.getNewses()) {
                n.setDate(getStringDateDashed(calendar));
                newsArrayList.add(n);
            }
            //check existence of quiz in database
            checkQuiz(calendar);

        }
    }

    public void checkQuiz(final Calendar passedDate){
        final String rawDate = getStringDateDashed(passedDate);

        Query query = FirebaseDatabase.getInstance().getReference("dailyQuiz")
                .orderByChild("date")
                .equalTo(getReverseDate(rawDate));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    News n = new News("quiz",rawDate);
                    newsArrayList.add(n);
                    currentAffairsArrayList.clear();
                    adapterNews.notifyDataSetChanged();
                }
                else {
                    currentAffairsArrayList.clear();
                    adapterNews.notifyDataSetChanged();
                }
                CurrentAffaris.stopAnimPragress(progressBar);
                isLoading = false;
                if(isLoadComplete) {
                    pager.setCurrentItem(0);
                    isLoadComplete = false;
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getStringDateDashed(Calendar calendar) {
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        if(d < 10 && m < 10) return "0"+d+"/0"+(m+1)+"/"+y;
        else if (m < 10) return ""+d+"/0"+(m+1)+"/"+y;
        else if(d < 10) return "0"+d+"/"+(m+1)+"/"+y;
        return ""+d+"/"+(m+1)+"/"+y;
    }

    private String getReverseDate(String date){
        String[] split = date.split("/");
        return split[2]+split[1]+split[0];
    }
    public void hide_unhide(boolean isSwiped){
        if(!isSwiped && datePicker.getVisibility() == View.INVISIBLE) datePicker.setVisibility(View.VISIBLE);
        else datePicker.setVisibility(View.INVISIBLE);
        if(navHideListener != null) navHideListener.onTouch(isSwiped);
    }

    @Override
    public void onDateChanged(String date) {
        String[] dates = date.split("/");
        Calendar c = Calendar.getInstance();
        c.set(Integer.valueOf(dates[2]),Integer.valueOf(dates[1])-1, Integer.valueOf(dates[0]));
        datePickerView.setDate(c);
    }
}
