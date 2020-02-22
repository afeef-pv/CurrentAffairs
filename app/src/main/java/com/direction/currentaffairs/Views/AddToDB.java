package com.direction.currentaffairs.Views;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.direction.currentaffairs.Models.CurrentAffairs;
import com.direction.currentaffairs.Models.News;
import com.direction.currentaffairs.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class AddToDB extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, View.OnClickListener {

    Button date, upload;
    DatePickerDialog datePickerDialog;
    Calendar calendar;
    String dateDate;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_db);

        databaseReference = FirebaseDatabase.getInstance().getReference("currentAffairs");

        date = findViewById(R.id.datePick);
        upload = findViewById(R.id.upload);

        date.setOnClickListener(this);
        upload.setOnClickListener(this);

        calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this,R.style.DialogTheme,this,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year,month,dayOfMonth);
        dateDate = ""+dayOfMonth+month+year;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.datePick:
                datePickerDialog.show();
                break;
            case R.id.upload:
                uploadToDB();
                break;
        }
    }

    private void uploadToDB() {
        if(dateDate != null){
            ArrayList<News> array = new ArrayList<>();

            String id = databaseReference.push().getKey();
            for(int i = 0; i < 5; ++i){
                News news = new News(dateDate + "News Heading"+ (i+1)
                        ,"The Award focuses on the triple-bottom-line of People, Profit, and Planet. It will be offered annually to the leader of a Nation." ,
                        "Prof. Philip Kotler is a world-renowned Professor of Marketing at Northwestern University, Kellogg School of Management. Owing to his ill-health, he deputed " ,
                        "Dr. Jagdish Sheth of EMORY University, Georgia, USA, to confer the award.");
                array.add(news);
            }

            CurrentAffairs currentAffairs = new CurrentAffairs(id,dateDate,array);
            databaseReference.child(id).setValue(currentAffairs);
            Toast.makeText(this,"Added", Toast.LENGTH_SHORT).show();
        }
    }
}
