package com.direction.currentaffairs.Views;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;

import com.direction.currentaffairs.R;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        auth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryAxn));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void onRegister(View view) {
        Animation wiggle;
        int duration = 300;
        EditText name = findViewById(R.id.spv_et_name);
        EditText phone = findViewById(R.id.spv_et_phone);
        EditText password = findViewById(R.id.spv_et_otp);

        if(name.getText().toString().length() < 3){
            name.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field_err));
            phone.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));
            password.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));

            wiggle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wiggle);
            name.startAnimation(wiggle);
            viberate(duration);

        }
        else if(phone.getText().toString().length() < 6){
            phone.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field_err));
            name.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));
            password.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));

            wiggle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wiggle);
            phone.startAnimation(wiggle);
            viberate(duration);
        }
        else if(password.getText().toString().length() < 5){
            password.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field_err));
            name.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));
            phone.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));

            wiggle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wiggle);
            password.startAnimation(wiggle);
            viberate(duration);
        }
        else {
            //Register
        }
    }

    private void viberate(int duration){
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(duration);
        }
    }
}
