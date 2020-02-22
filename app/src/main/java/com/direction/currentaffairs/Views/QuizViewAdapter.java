package com.direction.currentaffairs.Views;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class QuizViewAdapter extends FragmentPagerAdapter {
    Context ctxt;
    private String tabTitles[] = new String[]{"DailyQuiz", "WeeklyQuiz", "MonthlyQuiz"};

    public QuizViewAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                return DailyQuiz.newInstance("DailyQuiz, Instance 1");
            case 1:
                return WeeklyQuiz.newInstance("WeeklyQuiz, Instance 1");
            case 2:
                return MonthlyQuiz.newInstance("MonthlyQuiz, Default");
        }
        return DailyQuiz.newInstance("DailyQuiz, Instance 1");
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
