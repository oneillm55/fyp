package com.example.fyp.FlightFolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.R;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.MyViewHolder>{
    Context context;
    List<Flight> flightList;

    public FlightAdapter(Context context, List<Flight> flightList) {
        this.context = context;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public FlightAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.flights_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FlightAdapter.MyViewHolder holder, int position) {
        Flight flight = flightList.get(position);
        holder.arrival.setText(flight.getArrive());
        holder.depart.setText(flight.getDepart());
        holder.score.setText(flight.getScore());

    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView depart, arrival, score;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            arrival = itemView.findViewById(R.id.depart_name);
            depart = itemView.findViewById(R.id.arrive_name);
            score = itemView.findViewById(R.id.flight_score);
        }
    }
}
