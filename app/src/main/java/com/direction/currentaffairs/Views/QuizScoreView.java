package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;

public class QuizScoreView extends Fragment {

    View rootView;
    View.OnClickListener listener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.score_layout, container, false);

//        ImageView imageView = rootView.findViewById(R.id.score_top_art);
//        Glide.with(rootView)
//                .load(R.drawable.top_art)
//                .into(imageView);
//
//        imageView = rootView.findViewById(R.id.score_btm_art);
//        Glide.with(rootView)
//                .load(R.drawable.bottom_art)
//                .into(imageView);

        Button retest = rootView.findViewById(R.id.btn_retest);
        if(listener!= null) retest.setOnClickListener(listener);

        return rootView;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public static QuizScoreView newInstance(View.OnClickListener listener){
        QuizScoreView fragment = new QuizScoreView();
        fragment.setListener(listener);
        return fragment;
    }

    public void setContent(int type,String date,int correct, int incorrect, int total){

        int skipped = total - (correct+incorrect);
        String str;
        String[] types = {"Daily Quiz", "Weekly Quiz", "Monthly Quiz"};
        TextView textView = rootView.findViewById(R.id.scr_tv_correct);
        textView = rootView.findViewById(R.id.scr_tv_date);
        textView.setText(date);

        if(CurrentAffaris.displayName != null) {
            textView = rootView.findViewById(R.id.scr_tv_cngrts);
            textView.setText("Congratulations "+CurrentAffaris.displayName);
        }

        textView = rootView.findViewById(R.id.scr_tv_correct);
        if(correct > 10 && correct < 100) str = "0"+correct;
        else if (correct < 10) str = "00"+correct;
        else str = ""+correct;
        textView.setText(str);

        if(correct == 0) textView.setText("000");

        if(incorrect > 10 && incorrect < 100) str = "0"+incorrect;
        else if (incorrect < 10) str = "00"+incorrect;
        else str = ""+incorrect;
        textView = rootView.findViewById(R.id.scr_tv_incorrect);
        textView.setText(str);

        if(skipped > 10 && skipped < 100) str = "0"+skipped;
        else if (skipped < 10) str = "00"+skipped;
        else str = ""+skipped;
        textView = rootView.findViewById(R.id.scr_tv_skipped);
        textView.setText(str);

        textView = rootView.findViewById(R.id.scr_tv_quizname);
        textView.setText(types[type]);
    }
}
