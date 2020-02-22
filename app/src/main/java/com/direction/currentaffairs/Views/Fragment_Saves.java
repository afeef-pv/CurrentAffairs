package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.direction.currentaffairs.R;
import com.direction.currentaffairs.Utils.SavesAdapter;
import com.google.android.material.tabs.TabLayout;

public class Fragment_Saves extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.fragment_saves, container, false);
        ViewPager pager = result.findViewById(R.id.saves_viewPager);
        pager.setAdapter(buildAdapter());
        TabLayout tabLayout = result.findViewById(R.id.saves_tablayout);
        tabLayout.setupWithViewPager(pager);

        return(result);
    }

    private PagerAdapter buildAdapter() {
        return(new SavesAdapter(getActivity(), getChildFragmentManager()));
    }
}