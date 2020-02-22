package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.direction.currentaffairs.Adapters.AdapterSavedNews;
import com.direction.currentaffairs.Adapters.Divider;
import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.direction.currentaffairs.RealmModels.RealmNews;

import io.realm.RealmResults;


public class SavedNews extends Fragment {

    RecyclerView saveView;
    AdapterSavedNews adapterSavedNews;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.save_view_layout, container, false);

        saveView = rootView.findViewById(R.id.rv_saves);
        saveView.addItemDecoration(new Divider(getActivity(),LinearLayout.VERTICAL));
        saveView.setLayoutManager(new LinearLayoutManager(getActivity()));
        RealmResults<RealmNews> results = CurrentAffaris.read(getActivity());
        adapterSavedNews = new AdapterSavedNews(getActivity(), results,true);
        saveView.setAdapter(adapterSavedNews);
        CurrentAffaris.setAdapterSavedNews(adapterSavedNews);

        return rootView;
    }

    public static SavedNews newInstance() {

        Bundle args = new Bundle();

        SavedNews fragment = new SavedNews();
        fragment.setArguments(args);
        return fragment;
    }
}
