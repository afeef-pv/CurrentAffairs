package com.direction.currentaffairs.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.google.firebase.auth.FirebaseAuth;

public class Fragment_Settings extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings,container,false);
        final TextView emailView = rootView.findViewById(R.id.settings_tv_email);
        final TextView nameView = rootView.findViewById(R.id.settings_tv_name);

        if(CurrentAffaris.phoneOrEmail != null) emailView.setText(CurrentAffaris.phoneOrEmail);
        if(CurrentAffaris.phoneOrEmail != null) nameView.setText(CurrentAffaris.displayName);

        final FirebaseAuth auth = FirebaseAuth.getInstance();

        if(auth.getCurrentUser() != null){
            final Button button = rootView.findViewById(R.id.btn_signout);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(auth.getCurrentUser() != null)  {
                        emailView.setText("no user");
                        auth.signOut();
                        button.setVisibility(View.GONE);
                    }
                }
            });
        }

        return(rootView);
    }
}
