package com.example.fyp.FlightFolder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fyp.R;

public class ViewFlightsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_flights);
    }
//    public class LocationViewHolder extends RecyclerView.ViewHolder{
//
//        TextView arrive,depart,score;
//        LinearLayout flight;
//
//        public LocationViewHolder(@NonNull View itemView) {
//            super(itemView);
//            arrive=itemView.findViewById(R.id.arrive_name);
//            depart=itemView.findViewById(R.id.depart_name);
//            score=itemView.findViewById(R.id.flight_score);
//            flight=itemView.findViewById(R.id.flight_linear_layout);
//        }
//    }
}