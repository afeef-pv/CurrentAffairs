package com.direction.currentaffairs;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import com.direction.currentaffairs.Adapters.AdapterSavedNews;
import com.direction.currentaffairs.Models.News;
import com.direction.currentaffairs.RealmModels.RealmNews;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import com.direction.currentaffairs.Utils.SHA256;

public class CurrentAffaris extends Application {
    public static String phoneOrEmail;
    public static String displayName;
    public static final int DAILY = 0;
    public static final int WEEKLY = 1;
    public static final int MONTHLY = 2;
    private static AdapterSavedNews adapterSavedNews;
    public static final String[] MONTHS = new String[]{ "January",
                                                        "February",
                                                        "March",
                                                        "April",
                                                        "May",
                                                        "June",
                                                        "July",
                                                        "August",
                                                        "September",
                                                        "October",
                                                        "November",
                                                        "December" };

    @Override
    public void onCreate() {
        super.onCreate();
        checkAuth();
    }

    public static void checkAuth() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null){
            if(CurrentAffaris.displayName == null || CurrentAffaris.displayName.length() < 2){
                if(auth.getCurrentUser().getEmail() != null) {
                    CurrentAffaris.phoneOrEmail = auth.getCurrentUser().getEmail();
                    if(phoneOrEmail.length() > 2) CurrentAffaris.displayName = auth.getCurrentUser().getDisplayName();
                    else {
                        CurrentAffaris.phoneOrEmail = auth.getCurrentUser().getPhoneNumber();
                        Query query = FirebaseDatabase.getInstance().getReference("users")
                                .orderByChild("phone")
                                .equalTo(CurrentAffaris.phoneOrEmail);
                        query.addListenerForSingleValueEvent(displayNameListener);
                    }
                }
            }
        }
    }

    public static void animateProgress(View progressBar){
        ImageView imageView = progressBar.findViewById(R.id.anim_loading_iv);
        Drawable d = imageView.getDrawable();
        if(d instanceof AnimatedVectorDrawableCompat){
            final AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    avd.start();
                }
            });
            avd.start();
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(d instanceof AnimatedVectorDrawable){
                final AnimatedVectorDrawable avd = (AnimatedVectorDrawable) d;
                avd.registerAnimationCallback(new Animatable2.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        avd.start();
                    }
                });
                avd.start();
            }
        }
    }

    public static void stopAnimPragress(View progressBar){
        ImageView imageView = progressBar.findViewById(R.id.anim_loading_iv);
        Drawable d = imageView.getDrawable();
        if(d instanceof AnimatedVectorDrawableCompat){
            AnimatedVectorDrawableCompat avd = (AnimatedVectorDrawableCompat) d;
            avd.stop();
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(d instanceof AnimatedVectorDrawable){
                AnimatedVectorDrawable avd = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    avd = (AnimatedVectorDrawable) d;
                }
                avd.stop();
            }
        }
    }

    public static void slideDown(final View view){
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(200);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    public static void slideUp(final View view){
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(100);
        animate.setFillAfter(true);
        animate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(animate);
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration(200);
        v.startAnimation(a);
    }

    static final ValueEventListener displayNameListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                //exists
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    CurrentAffaris.displayName = snapshot.child("name").getValue(String.class);
                }
            } else {
                //Not registered
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };

    public static String formatDate(String date){
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

    public static boolean check(Realm realm, String id){
        RealmNews news = realm.where(RealmNews.class).equalTo("id",id).findFirst();
        if(news == null) return true;
        else return false;
    }

    public static void save(Context context, News news){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        String id;
        try {
            id = SHA256.toHexString(news.getHeading());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            id = "none";
        }
        if(check(realm, id)){
            RealmNews realmNews = new RealmNews(id,news.getHeading(), news.getUrl(), news.getDate(),news.getSubpoints());
            realm.beginTransaction();
            realm.copyToRealm(realmNews);
            realm.commitTransaction();
            realm.close();
            Toast.makeText(context,"Saved",Toast.LENGTH_SHORT).show();
        }else  Toast.makeText(context,"Already Saved",Toast.LENGTH_SHORT).show();
    }

    public static RealmResults<RealmNews> read(Context context){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmNews> results = realm.where(RealmNews.class).findAll();
//        RealmList<RealmNews> arrayList = new RealmList<>();
//        for(RealmNews n: results) arrayList.add(n);
        return results;
    }

    public static RealmNews read(Context context, String id){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        RealmResults<RealmNews> results = realm.where(RealmNews.class)
                                                .equalTo("id", id)
                                                .findAll();
        return results.get(0);
    }

    public static void delete(final Context context, final String id, final OnFinishActivity onFinishActivity){
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder().build();
        Realm.setDefaultConfiguration(configuration);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                RealmResults<RealmNews> result = realm.where(RealmNews.class)
                                                        .equalTo("id",id)
                                                        .findAll();
                result.deleteAllFromRealm();
                Toast.makeText(context,"Deleted",Toast.LENGTH_SHORT).show();
                onFinishActivity.onFinish();
            }
        });
    }

    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public static void shareWhatsapp(Context context, Bitmap bitmap, String description){
        try {

            File cachePath = new File(context.getCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            File imagePath = new File(context.getCacheDir(), "images");
            File newFile = new File(imagePath, "image.png");
            Uri imgUri = FileProvider.getUriForFile(context, "com.direction.currentaffairs.fileprovider", newFile);
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, description);
            whatsappIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
            whatsappIntent.setType("image/jpeg");
            whatsappIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                context.startActivity(whatsappIntent);
            } catch (android.content.ActivityNotFoundException ex) {
                //No whatsapp
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setAdapterSavedNews(AdapterSavedNews adapterSavedNews) {
        CurrentAffaris.adapterSavedNews = adapterSavedNews;
    }

    public interface OnFinishActivity{
        void onFinish();
    }
}
