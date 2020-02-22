package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;

public class AdsFragment extends Fragment implements View.OnClickListener {

    Button gotoButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_ads,container,false);

        TextView closeButton = rootView.findViewById(R.id.ads_close);
        closeButton.setOnClickListener(this);
        gotoButton = rootView.findViewById(R.id.goto_offer);
        gotoButton.setOnClickListener(this);

        return(rootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ads_close:
                getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                break;
            case R.id.goto_offer:
                CurrentAffaris.collapse(gotoButton);
        }
    }
}
