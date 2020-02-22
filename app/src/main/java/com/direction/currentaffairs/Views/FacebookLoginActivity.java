package com.direction.currentaffairs.Views;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.direction.currentaffairs.CurrentAffaris;
import com.direction.currentaffairs.Models.User;
import com.direction.currentaffairs.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class FacebookLoginActivity extends AppCompatActivity {

    private static final String TAG = "FBERROR";
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private String phoneVerificationId;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private boolean isRegistered;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook_login);

        progressBar = findViewById(R.id.spv_progressBar);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryAxn));
        }

        initFb();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) updateUI(currentUser);
    }

    //MAIN
    private void updateUI(FirebaseUser currentUser) {
        if(currentUser != null){
            if(currentUser.getEmail() != null) CurrentAffaris.phoneOrEmail = currentUser.getEmail();
            finish();
        }else{
            Toast.makeText(FacebookLoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void initFb(){
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.buttonFacebookLogin);
        loginButton.setReadPermissions("phoneOrEmail", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // ...
            }
        });


        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// ...

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(FacebookLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

    public void getOtp(View view) {
        EditText editText = findViewById(R.id.spv_et_phone);
        String phoneNo = editText.getText().toString();
        if(phoneNo.length() > 9){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                editText.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field));
            }
            TextView textView = findViewById(R.id.resendTv);
            textView.setVisibility(View.VISIBLE);
            Button button = findViewById(R.id.spv_btn_getotp);
//                button.setVisibility(View.GONE);
            CurrentAffaris.collapse(button);
            checkUser(phoneNo);
        }
        else {
            Animation wiggle;
            wiggle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wiggle);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                editText.setBackground(ContextCompat.getDrawable(this,R.drawable.bg_input_field_err));
            }
            editText.startAnimation(wiggle);
            viberate(200);
        }

    }

    public void proceedToLogin(String phoneNo){

        setUpVerificationCallbacks();
        Log.d(TAG, "proceedToLogin: "+phoneNo);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks
        );
    }

    private void setUpVerificationCallbacks() {
        verificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(FacebookLoginActivity.this,"Failed",Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                phoneVerificationId = verificationId;
                resendingToken = forceResendingToken;

                Button button;

                EditText editText = findViewById(R.id.spv_et_otp);
                editText.setVisibility(View.VISIBLE);

                if(isRegistered){
                    button = findViewById(R.id.spv_btn_login);
                    button.setVisibility(View.VISIBLE);
                }else{
                    editText = findViewById(R.id.spv_et_name);
                    editText.setVisibility(View.VISIBLE);
                    button = findViewById(R.id.spv_btn_register);
                    button.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    public void verifyCode(){
        EditText editText = findViewById(R.id.spv_et_otp);
        String code = editText.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(phoneVerificationId,code);
        signInWithPhone(credential);
    }

    private void signInWithPhone(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            //Success
                            FirebaseUser user = task.getResult().getUser();
                            updateUI(user);
                        }else{
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                EditText editText = findViewById(R.id.spv_et_otp);
                                Animation wiggle;
                                wiggle = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.wiggle);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    editText.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bg_input_field_err));
                                }
                                editText.startAnimation(wiggle);
                                viberate(200);
                            }
                        }
                    }
                });
    }

    public void checkUser(String phoneNo){
        progressBar.setVisibility(View.VISIBLE);
        Query query = FirebaseDatabase.getInstance().getReference("users")
                .orderByChild("phone")
                .equalTo(phoneNo);
        query.addListenerForSingleValueEvent(valueEventListener);
    }

    final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                //exists
                isRegistered = true;
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CurrentAffaris.displayName = snapshot.child("name").getValue(String.class);
                    CurrentAffaris.phoneOrEmail = snapshot.child("phone").getValue(String.class);
                }
                proceedToLogin(CurrentAffaris.phoneOrEmail);
            } else {
                //Not registered
                isRegistered = false;
                proceedToRegister();
            }
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public void login(View view) {
        verifyCode();
    }

    private void proceedToRegister() {
        EditText editText = findViewById(R.id.spv_et_phone);
        editText.setVisibility(View.VISIBLE);
        String phoneNo = editText.getText().toString();
        editText = findViewById(R.id.spv_et_name);
        editText.setVisibility(View.VISIBLE);

        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks
        );
    }

    public void register(View view) {
        EditText editText = findViewById(R.id.spv_et_phone);
        String phone = editText.getText().toString();

        editText = findViewById(R.id.spv_et_name);
        String name = editText.getText().toString();

        registerUser(phone, name);
    }

    private void registerUser(String phone, String name) {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        String id = databaseReference.push().getKey();
        User user = new User(phone, name);
        databaseReference.child(id).setValue(user);
        verifyCode();
        Log.d(TAG, "registerUser: ");
    }

    public void resendCode(View view) {

        EditText editText = findViewById(R.id.spv_et_phone);
        String phoneNo = editText.getText().toString();

        setUpVerificationCallbacks();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNo,
                60,
                TimeUnit.SECONDS,
                this,
                verificationStateChangedCallbacks,
                resendingToken
        );
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
