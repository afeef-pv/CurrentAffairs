package com.direction.currentaffairs.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.Models.Question;
import com.direction.currentaffairs.Models.Quiz;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Widgets.CustomGrid;
import com.direction.currentaffairs.Widgets.FCViewPager;
import com.direction.currentaffairs.Widgets.QuestionHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class QuizViewPager extends FragmentActivity implements AdapterView.OnItemClickListener {

    private static final int QCELL = 0;
    public static final int SCELL = 1;
    //load quiz from server
    private ArrayList<QuestionHolder> questionHolders = new ArrayList<>();
    public static int currentQuestion;
    FCViewPager pager;
    LinearLayout menu;
    View menuBg;
    GridView questions_grid;
    CustomGrid gridAdapter;
    Toolbar toolbar;
    QuizScoreView quizScoreView;
    public static boolean isMenuShowing = false;
    TextView questionNo, dateView;
    private int score = 0, incorrect = 0;
    MyPagerAdapter quizAdapter;
    View progressBar;
    FirebaseAuth auth;

    private Quiz quiz;
    private int type;
    private String databaseUrl;
    String quizName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_view_pager);

        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){

            CurrentAffaris.checkAuth();

            progressBar = findViewById(R.id.progressBar);

            toolbar = findViewById(R.id.toolbar);
            ImageButton button = toolbar.findViewById(R.id.btn_slide_menu);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showMenu();
                }
            });
            dateView = toolbar.findViewById(R.id.tb_tv_date);

            quizName = getIntent().getExtras().getString("id");
            dateView.setText(formatDate(quizName));

            type = Integer.parseInt(getIntent().getExtras().getString("type"));

            if(type == CurrentAffaris.DAILY)
                databaseUrl = "dailyQuiz";
            else if(type == CurrentAffaris.WEEKLY)
                databaseUrl = "weeklyQuiz";
            else databaseUrl = "monthlyQuiz";

            menu = findViewById(R.id.menu_questions_in_cell);
            menuBg = menu.findViewById(R.id.menu_background);
            menuBg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(v.getId() == R.id.menu_background){
                        if(menu.getVisibility() == View.VISIBLE) {
                            hideMenu();
                        }
                        else menu.setVisibility(View.VISIBLE);
                    }
                }
            });

            //Load quiz
            loadData(quizName);
        }else{
            Intent intent = new Intent(this,FacebookLoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryAxn));
        }   currentQuestion = 1;

        pager = (FCViewPager) findViewById(R.id.quizViewPager);
        quizAdapter = new MyPagerAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        pager.setAdapter(quizAdapter);

        questions_grid = menu.findViewById(R.id.grid_questions);

        gridAdapter = new CustomGrid(this, questionHolders);
        questions_grid.setAdapter(gridAdapter);
        questions_grid.setOnItemClickListener(this);

        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setQuestionNo();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        pager.setCurrentItem(position,true);
        QuestionHolder questionHolder = questionHolders.get(position);
        if(questionHolder.getStatus() != QuestionHolder.CORRECT && questionHolder.getStatus() != QuestionHolder.INCORRECT) questionHolder.setStatus(QuestionHolder.SKIPPED);

        gridAdapter.notifyDataSetChanged();
        questions_grid.setAdapter(gridAdapter);
        hideMenu();
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

//        public MyPagerAdapter(FragmentManager fm) {
//            super(fm);
//        }

        @Override
        public Fragment getItem(final int pos) {
            if(pos < questionHolders.size()) {
                QuestionView.OnOptionSelect optionSelect = new QuestionView.OnOptionSelect() {
                    @Override
                    public void onCorrect(boolean isCorrect) {
                        if (isCorrect){
                            pager.setCurrentItem(pos + 1, true);
                            score++;
                        }else{
                            incorrect++;
                        }
                        gridAdapter.notifyDataSetChanged();
                        questions_grid.setAdapter(gridAdapter);
                    }
                };
                QuestionView questionView = QuestionView.newInstance(QuizViewPager.this, questionHolders.get(pos), optionSelect);

                return questionView;
            }else {
                quizScoreView = QuizScoreView.newInstance(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });
                return quizScoreView;
            }
        }

        @Override
        public int getCount() {
            return questionHolders.size()+1;
        }

    }

    public void setQuestionNo(){

        if(getViewType(pager.getCurrentItem()) == QCELL) {
            questionNo = toolbar.findViewById(R.id.no_qstns);
            questionNo.setText(""+(pager.getCurrentItem()+1)+"/"+questionHolders.size());

            if(questionHolders.get(pager.getCurrentItem()).getStatus() == QuestionHolder.UNSEEN) {
                questionHolders.get(pager.getCurrentItem()).setStatus(QuestionHolder.SKIPPED);
                gridAdapter.notifyDataSetChanged();
            }
        }else{
            if(quizScoreView != null) quizScoreView.setContent(type, formatDate(quizName), score, incorrect,
                    questionHolders.size());
        }
    }

    private int getViewType(int pos){
        if(pos < questionHolders.size()) return QCELL;
        else return SCELL;
    }

    public void showMenu(){
        menu.setVisibility(View.VISIBLE);

        menu.animate()
            .setDuration(200)
            .translationX(0)
            .alpha(1.0f)
            .setListener(null);
        pager.setEnableSwipe(false);
        isMenuShowing = true;
    }

    private void hideMenu() {
        menu.animate()
                .setDuration(100)
                .translationX(0)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        menu.setVisibility(View.GONE);
                    }
                });
        pager.setEnableSwipe(true);
        isMenuShowing = false;
    }

    public void loadData(String date){
        progressBar.setVisibility(View.VISIBLE);
        CurrentAffaris.animateProgress(progressBar);
        Query query = FirebaseDatabase.getInstance().getReference(databaseUrl)
                .orderByChild("rawDate")
                .equalTo(date);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    quiz = snapshot.getValue(Quiz.class);
                }
                int i = 0;
                for(Question q : quiz.getQuestions()) {
                    QuestionHolder h = new QuestionHolder();
                    h.setTitle(""+(i+1));
                    h.setContent(q);
                    h.setStatus(QuestionHolder.UNSEEN);
                    questionHolders.add(h);
                    ++i;
                }
                progressBar.setVisibility(View.INVISIBLE);
                CurrentAffaris.stopAnimPragress(progressBar);
                init();
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

    private String formatDate(String date){
        date = date.replace("-","/");
        String[] retDate;
        retDate = date.split("/");
        Log.d("SHOUT", "onScrolled: "+date);

        if(retDate.length < 3)
            return CurrentAffaris.MONTHS[Integer.parseInt(retDate[0])-1]+
                    " "+retDate[1];

        return retDate[0]+" "+
                CurrentAffaris.MONTHS[Integer.parseInt(retDate[1])-1]+
                " "+retDate[2];

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogTheme))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit Quiz")
                .setMessage("Are you sure you want to close this quiz?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();

    }
}