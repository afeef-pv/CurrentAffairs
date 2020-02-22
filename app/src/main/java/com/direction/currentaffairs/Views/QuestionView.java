package com.direction.currentaffairs.Views;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.direction.currentaffairs.Models.Question;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Widgets.QuestionHolder;

public class QuestionView extends Fragment implements View.OnTouchListener, View.OnClickListener {

    private Question question;
    private QuestionHolder questionHolder;
//    private int adnswerId = -1, fslag = 0, usserOption = -1;
    View rootView;
    OnOptionSelect onOptionSelect;
    QuizViewPager parentActivity;


    public QuestionView(QuizViewPager activity, QuestionHolder questionHolder, OnOptionSelect onOptionSelect) {
        this.questionHolder = questionHolder;
        question = questionHolder.getContent();
        parentActivity = activity;
        setOnOptionSelect(onOptionSelect);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.quiz_cell, container, false);
        TextView questionView = rootView.findViewById(R.id.qc_question);
        questionView.setText(question.getQuestion());

        setOption(R.id.opt_a,question.getOpt_a());
        setOption(R.id.opt_b,question.getOpt_b());
        setOption(R.id.opt_c,question.getOpt_c());
        setOption(R.id.opt_d,question.getOpt_d());
        setOption(R.id.opt_e,question.getOpt_e());

        if(questionHolder.userOption != -1){
            if(questionHolder.userOption == questionHolder.answerId){
                TextView textView = rootView.findViewById(questionHolder.answerId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_crt));
                }
            }else {
                TextView textView = rootView.findViewById(questionHolder.answerId);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_crt));
                }
                textView = rootView.findViewById(questionHolder.userOption);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_wrng));
                }
            }
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(parentActivity.gridAdapter != null)
            parentActivity.setQuestionNo();
    }

    private void setOption(int id, String option){
        TextView optionView = rootView.findViewById(id);
        optionView.setText(option);
        optionView.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            optionView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option));
        }
        if(questionHolder.answerId == -1 && option.equals(question.getAnswer())) questionHolder.answerId = id;

    }

    public static QuestionView newInstance(QuizViewPager activity, QuestionHolder questionHolder, OnOptionSelect onOptionSelect) {

        QuestionView f = new QuestionView(activity, questionHolder, onOptionSelect);
        Bundle b = new Bundle();
        f.setArguments(b);

        return f;
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    private void checkAnswer(int optId) {
        questionHolder.flag = 1;
        questionHolder.userOption = optId;
        TextView textView = rootView.findViewById(questionHolder.answerId);
        if(optId == questionHolder.answerId){
            //next question
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_crt));
                textView.getCompoundDrawables()[0].setTint(Color.GREEN);
            }
            questionHolder.setStatus(QuestionHolder.CORRECT);
            onOptionSelect.onCorrect(true);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textView = rootView.findViewById(optId);
                textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_wrng));
                textView.getCompoundDrawables()[0].setTint(Color.RED);
                questionHolder.setStatus(QuestionHolder.INCORRECT);
                textView = rootView.findViewById(questionHolder.answerId);
                textView.setBackground(ContextCompat.getDrawable(getActivity(),R.drawable.bg_option_crt));
                textView.getCompoundDrawables()[0].setTint(Color.GREEN);
                onOptionSelect.onCorrect(false);
            }
        }
        if(question.getExtra() != null){
            TextView extra = rootView.findViewById(R.id.extra_notes);
            extra.setText(question.getExtra());
            extra.setVisibility(View.VISIBLE);
            extra = rootView.findViewById(R.id.extra);
            extra.setVisibility(View.VISIBLE);
        }

    }

    public void setOnOptionSelect(OnOptionSelect onOptionSelect) {
        this.onOptionSelect = onOptionSelect;
    }

    @Override
    public void onClick(View v) {
        if(QuizViewPager.isMenuShowing != true && questionHolder.flag == 0){
            switch (v.getId()){
                case R.id.opt_a:
                    checkAnswer(R.id.opt_a);
                    break;
                case R.id.opt_b:
                    checkAnswer(R.id.opt_b);
                    break;
                case R.id.opt_c:
                    checkAnswer(R.id.opt_c);
                    break;
                case R.id.opt_d:
                    checkAnswer(R.id.opt_d);
                    break;
                case R.id.opt_e:
                    checkAnswer(R.id.opt_e);
                    break;
            }
            return;
        }
//
        return;
    }

    public interface OnOptionSelect{
        void onCorrect(boolean isCorrect);
    }
}
