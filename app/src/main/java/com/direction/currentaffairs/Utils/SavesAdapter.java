package com.direction.currentaffairs.Utils;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.direction.currentaffairs.Views.SavedNews;
import com.direction.currentaffairs.Views.WeeklyQuiz;

public class SavesAdapter extends FragmentPagerAdapter {
    Context ctxt;
    private String tabTitles[] = new String[]{"News", "Questions"};

    public SavesAdapter(Context ctxt, FragmentManager mgr) {
        super(mgr, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.ctxt=ctxt;
    }

    @Override
    public int getCount() {
        return(1);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {

            case 0:
                return SavedNews.newInstance();
            case 1:
                return WeeklyQuiz.newInstance("Quiz, Instance 1");
        }
        return SavedNews.newInstance();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
