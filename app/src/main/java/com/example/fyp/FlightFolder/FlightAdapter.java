package com.example.fyp.FlightFolder;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.utils.widget.ImageFilterButton;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Footprint;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class FlightAdapter extends RecyclerView.Adapter<FlightAdapter.MyViewHolder>{
    Context context;
    List<Flight> flightList;



    public FlightAdapter(Context context, List<Flight> flightList) {
        this.context = context;
        this.flightList = flightList;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView arrive, depart, flightClass, footprint, returnFlight;
        private ImageFilterButton deleteButton;
        private FirebaseAuth firebaseAuth;
        private DatabaseReference mDatabase;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            firebaseAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();
            arrive = itemView.findViewById(R.id.depart_name);
            depart = itemView.findViewById(R.id.arrive_name);
            flightClass = itemView.findViewById(R.id.flight_class);
            footprint = itemView.findViewById(R.id.flight_footprint);
            returnFlight = itemView.findViewById(R.id.flight_return);
            deleteButton= itemView.findViewById(R.id.delete_flight);
        }
    }

    @NonNull
    @Override
    public FlightAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View flightView = LayoutInflater.from(parent.getContext()).inflate(R.layout.flights_layout, parent,false);
        return new MyViewHolder(flightView);
    }

    @Override
    public void onBindViewHolder(@NonNull FlightAdapter.MyViewHolder holder, int position) {
        Flight flight = flightList.get(position);
        holder.arrive.setText(flight.getArrive());
        holder.depart.setText(flight.getDepart());
        holder.flightClass.setText(flight.getFlightClass());
        holder.footprint.setText(String.valueOf(flight.getFootprint()));
        holder.returnFlight.setText(String.valueOf(flight.getReturnFlight()));
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "Delete Flight Clicked", Toast.LENGTH_SHORT).show();
                new AlertDialog.Builder(view.getContext())
                                .setTitle("Delete Flight")
                                .setMessage("Are you sure you want to delete this flight?")

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //delete flight
                                        holder.mDatabase.child("flights").child(holder.firebaseAuth.getUid()).child(flight.getFlightID()).removeValue();
                                        flightList.clear();
                                        //update footprint total
                                        updateUserTotalFootprint(holder.mDatabase,holder.firebaseAuth.getUid(),flight.getFootprint());

                                        notifyDataSetChanged();

                                    }
                                })

                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    public interface recyclerOnClickListener{
        void onClick(View v, int position);
    }

    private void updateUserTotalFootprint(DatabaseReference db,String uid, double d) {
        db.child("footprint").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            double newFlightFootprint;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    newFlightFootprint = footprint.getFlight() - d;
                    db.child("footprint").child(uid).child("flight").setValue(newFlightFootprint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

}
