package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.Adapters.AdapterNews;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.Models.CurrentAffairs;
import com.direction.currentaffairs.Models.News;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Utils.EndlessRecyclerOnScrollListener;
import com.direction.currentaffairs.Utils.SnapHelperOneByOne;
import com.direction.currentaffairs.Widgets.DatePickerView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;

public class Fragment_Read extends Fragment{

    RecyclerView newsView;
    View rootView;
    AdapterNews adapterNews;
    CardView datePicker;
    MainActivity.NavHideListener navHideListener;
    ArrayList<News> newsArrayList;
    ArrayList<CurrentAffairs> currentAffairsArrayList;
    DatabaseReference databaseCurrentAffairs;
    Calendar calendar, lastCalender;
    DatePickerView datePickerView;
    LinearLayoutManager linearLayoutManager;
    View progressBar;

    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    Fragment_Read(MainActivity.NavHideListener navHideListener){
        setNavHideListener(navHideListener);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_read,null);
        calendar = Calendar.getInstance();

        currentAffairsArrayList = new ArrayList<>();
        databaseCurrentAffairs = FirebaseDatabase.getInstance().getReference("currentAffairs");

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

        linearLayoutManager = new LinearLayoutManager(getActivity());
        newsView = rootView.findViewById(R.id.rv_main);
        newsView.setLayoutManager(linearLayoutManager);

        datePicker = rootView.findViewById(R.id.date_card_view);

        newsArrayList = new ArrayList<>();

        adapterNews = new AdapterNews(getActivity(), newsArrayList);
        newsView.setAdapter(adapterNews);

        loadData(getStringDateDashed(calendar));
//        lastCalender = calendar;
        endlessRecyclerOnScrollListener =new EndlessRecyclerOnScrollListener(linearLayoutManager){
            @Override
            public void onLoadMore(int current_page) {
//                lastCalender = calendar;
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                loadData(getStringDateDashed(calendar));
            }

            @Override
            public void onScrolled() {
                if(navHideListener != null) navHideListener.onTouch(true);
                datePicker.setVisibility(View.INVISIBLE);
            }
        };
        newsView.addOnScrollListener(endlessRecyclerOnScrollListener);
        adapterNews.setCellTouchListener(new AdapterNews.OnTouchCell() {
            @Override
            public void onTouch() {
                hide_unhide();
            }
        });
        LinearSnapHelper linearSnapHelper = new SnapHelperOneByOne();
        linearSnapHelper.attachToRecyclerView(newsView);
        return rootView;
    }

    private void loadData_complete(String date) {
        currentAffairsArrayList.clear();
        newsArrayList.clear();
        loadData(date);
        newsView.getLayoutManager().scrollToPosition(0);

        newsView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    public void loadData(String date){
        date = getReverseDate(date);
        Query query = FirebaseDatabase.getInstance().getReference("currentAffairs")
                .orderByChild("date")
                .equalTo(date);
        query.addListenerForSingleValueEvent(valueEventListener);


        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);

    }

    public void reloadAdapter(){
        for(CurrentAffairs ca : currentAffairsArrayList) {
            for (News n : ca.getNewses()) newsArrayList.add(n);
            //check existence of quiz in database
            checkQuiz(calendar);

        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CurrentAffairs ca = snapshot.getValue(CurrentAffairs.class);
                    currentAffairsArrayList.add(ca);
                }
                reloadAdapter();
                datePickerView.setDate(calendar);
            }
            else {
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

    public void checkQuiz(final Calendar passedDate){
        final String rawDate = getStringDateDashed(passedDate);

        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);

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

                progressBar.setVisibility(View.GONE);
                CurrentAffaris.stopAnimPragress(progressBar);
                endlessRecyclerOnScrollListener.setTrue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void hide_unhide(){
        if(datePicker.getVisibility() == View.INVISIBLE) datePicker.setVisibility(View.VISIBLE);
        else datePicker.setVisibility(View.INVISIBLE);
        if(navHideListener != null) navHideListener.onTouch(false);
    }

    public void setNavHideListener(MainActivity.NavHideListener navHideListener) {
        this.navHideListener = navHideListener;
    }

    private String getStringDate(Calendar calendar) {
        int d = calendar.get(Calendar.DAY_OF_MONTH);
        int m = calendar.get(Calendar.MONTH);
        int y = calendar.get(Calendar.YEAR);
        return ""+d+m+y;
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
}
