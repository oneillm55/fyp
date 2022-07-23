package com.example.fyp.FlightFolder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fyp.R;

public class FlightDisplayActivity extends AppCompatActivity {

    private TextView departText,arriveText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_display);
        Intent intent = getIntent();
        String depart = intent.getExtras().getString("depart");
        String arrive = intent.getExtras().getString("arrive");
        departText= findViewById(R.id.textViewDepart);
        arriveText = findViewById(R.id.textViewArrive);
        departText.setText(depart);
        arriveText.setText(arrive);



    }
}