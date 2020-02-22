package com.direction.currentaffairs.Views;

/*
    بِسْمِ اللهِ الرَّحْمٰنِ الرَّحِيْم
    لحمد لله [نحمده] ونستعينه ونستغفره
     ونعوذ بالله من شرور أنفسنا [ومن سيئات أعمالنا] من يهده الله فلا مضل له ومن يضلل فلا هادي له
     وأشهد أن لا إله إلا الله وحده لا شريك له] وأشهد أن محمدا عبده ورسوله
 */

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView navView;
    View shadow;
    int flag = 0;
    NavHideListener navHideListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryAxn));
        }
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);
        shadow = findViewById(R.id.nav_view_shadow);
        navHideListener = new NavHideListener() {
            @Override
            public void onTouch(boolean isSwiped) {
                if(!isSwiped) hide_unhide();
                else{
                    if(navView.getVisibility() == View.VISIBLE) navView.setVisibility(View.INVISIBLE);
//                        CurrentAffaris.slideDown(navView);
                    shadow.setVisibility(View.INVISIBLE);
                    flag = 1;
                }
            }
        };
//        loadAds();
        Fragment fragment = new NewsViewPager(navHideListener);
        loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){
            Bundle bundle = new Bundle();
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,fragment)
                    .commit();
            return true;
        }
        return false;
    }

    private void loadAds(){
        Fragment fragment = new AdsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_ads,fragment)
                .commit();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.navigation_home:
                fragment = new NewsViewPager(navHideListener);
                break;

            case R.id.navigation_quiz:
                fragment = new Fragment_Quiz();
                break;

            case R.id.navigation_settings:
                fragment = new Fragment_Settings();
                break;

            case R.id.navigation_saves:
                fragment = new Fragment_Saves();
                break;

        }
        return loadFragment(fragment);
    }

    public void hide_unhide(){
        if(flag == 0) {
            navView.setVisibility(View.INVISIBLE);
//            CurrentAffaris.slideDown(navView);
            shadow.setVisibility(View.INVISIBLE);
            flag = 1;
        }else{
//            CurrentAffaris.slideUp(navView);
            navView.setVisibility(View.VISIBLE);
            flag = 0;
        }
    }

    public interface NavHideListener{
        void onTouch(boolean isSwiped);
    }
}
