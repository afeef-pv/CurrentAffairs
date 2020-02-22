package com.direction.currentaffairs.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Views.QuizViewPager;

import java.util.ArrayList;

public class AdapterQuiz extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater inflater;
    private ArrayList<String> quizes;
    private Context context;
    private int type;


    public AdapterQuiz(Context context,ArrayList<String> quizes, int type){
        if(context != null){
            inflater = LayoutInflater.from(context);
            this.context = context;
            this.quizes = quizes;
            this.type = type;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.row_quiz,parent,false);
        return new AdapterQuiz.ItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdapterQuiz.ItemHolder viewHolder = (ItemHolder) holder;
        if(type == CurrentAffaris.DAILY) {
            viewHolder.quizTitle.setText(formatDailyDate(quizes.get(position)));
            viewHolder.setDate(quizes.get(position),CurrentAffaris.DAILY);
        }
        else if (type == CurrentAffaris.WEEKLY) {
            viewHolder.quizTitle.setText(formatWeeklyDate(quizes.get(position)));
            viewHolder.setDate(quizes.get(position), CurrentAffaris.WEEKLY);
        }
        else {
            viewHolder.quizTitle.setText(formatMonthlyDate(quizes.get(position)));
            viewHolder.setDate(quizes.get(position), CurrentAffaris.MONTHLY);
        }
    }

    @Override
    public int getItemCount() {
        return quizes.size();
    }

    private String formatDailyDate(String date){
        String[] retDate = date.split("/");
        return retDate[0]+" "+
                CurrentAffaris.MONTHS[Integer.parseInt(retDate[1])-1]+
                " "+retDate[2];

    }

    private String formatWeeklyDate(String date){
        String[] retDate = date.split("-");
        return "Week "+retDate[0]+" "+
                CurrentAffaris.MONTHS[Integer.parseInt(retDate[1])-1]+
                " "+retDate[2];

    }

    private String formatMonthlyDate(String date){
        String[] retDate = date.split("-");
        return CurrentAffaris.MONTHS[Integer.parseInt(retDate[0])-1]+
                " "+retDate[1];

    }

    public class ItemHolder extends RecyclerView.ViewHolder{
        TextView quizTitle;
        Button startQuiz;
        LinearLayout linearLayout;
        AnimatorSet animatorSet;

        public ItemHolder(@NonNull View itemView) {
            super(itemView);
            quizTitle = itemView.findViewById(R.id.qz_title);
            startQuiz = itemView.findViewById(R.id.btn_strt_qz);
            linearLayout = itemView.findViewById(R.id.linearLayout_glow);

            animatorSet = new AnimatorSet();

            ObjectAnimator fadeout = ObjectAnimator.ofFloat(linearLayout, "alpha", 0.5f,0.1f);
            fadeout.setDuration(500);

            ObjectAnimator fadein = ObjectAnimator.ofFloat(linearLayout, "alpha", 0.1f,0.5f);
            fadein.setDuration(500);

            animatorSet.play(fadein).after(fadeout);

            animatorSet.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    animatorSet.start();
                }
            });

            animatorSet.start();

        }

        public void setDate(final String date, final int type){
            startQuiz.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QuizViewPager.class);
                    //put quiz id to intent and load from database
                    intent.putExtra("id",date);
                    intent.putExtra("type",""+type);
                    context.startActivity(intent);
                }
            });
        }
    }
}
