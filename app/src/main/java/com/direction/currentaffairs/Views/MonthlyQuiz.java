package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.Adapters.AdapterQuiz;
import com.direction.currentaffairs.Adapters.Divider;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MonthlyQuiz extends Fragment {

    RecyclerView quizView;
    AdapterQuiz adapterQuiz;
    View progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quiz_view_layout, container, false);

        progressBar = rootView.findViewById(R.id.progressBar);
        quizView = rootView.findViewById(R.id.rv_quiz);

        loadData();

        return rootView;
    }

    private void init(ArrayList<String> dates){

        quizView.setLayoutManager(new LinearLayoutManager(getActivity()));
        quizView.addItemDecoration(new Divider(getActivity(), LinearLayout.VERTICAL));
        adapterQuiz = new AdapterQuiz(getActivity(),dates, CurrentAffaris.MONTHLY);
        quizView.setAdapter(adapterQuiz);
    }

    public void loadData(){
        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);
        Query query = FirebaseDatabase.getInstance().getReference("monthlyQuiz")
                .orderByChild("date");
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                ArrayList<String> rdates = new ArrayList<>();
                ArrayList<String> dates = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String date = snapshot.child("rawDate").getValue(String.class);
                    dates.add(date);
                }
                for(int i = dates.size() - 1; i >= 0; --i)
                    rdates.add(dates.get(i));
                progressBar.setVisibility(View.INVISIBLE);
                CurrentAffaris.stopAnimPragress(progressBar);
                init(rdates);
            }
            else {
                progressBar.setVisibility(View.INVISIBLE);
                CurrentAffaris.stopAnimPragress(progressBar);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public static MonthlyQuiz newInstance(String text) {

        MonthlyQuiz f = new MonthlyQuiz();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
