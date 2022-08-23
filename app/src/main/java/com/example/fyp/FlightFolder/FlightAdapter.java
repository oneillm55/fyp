package com.example.fyp.FlightFolder;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        private ImageView arrowImage;
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
            deleteButton= itemView.findViewById(R.id.delete_flight);
            arrowImage=itemView.findViewById(R.id.arrowImage);
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
        holder.footprint.setText(String.valueOf(flight.getFootprint()+"t CO2"));
        if(flight.getReturnFlight()){
            holder.arrowImage.setImageResource(R.drawable.ic_baseline_compare_arrows_24);
        }else{

            holder.arrowImage.setImageResource(R.drawable.ic_baseline_arrow_forward_24);
        }
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AlertDialog.Builder(view.getContext())
                                .setTitle("Delete Flight")
                                .setMessage("Are you sure you want to delete this flight?")

                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //delete flight
                                        holder.mDatabase.child(holder.firebaseAuth.getUid()).child("flights").child(flight.getFlightID()).removeValue();
                                        notifyDataSetChanged();
                                        flightList.clear();// clear the flight list so that the same flights arent displayed on reload
                                        //update footprint total
                                        updateUserTotalFootprint(holder.mDatabase,holder.firebaseAuth.getUid(),flight.getFootprint());
//                                     error this is working to update the database but the total displayed on the flight page doesnt update
//                                        notifyDataSetChanged();


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
        db.child(uid).child("footprint").addListenerForSingleValueEvent(new ValueEventListener() {
            double newFlightFootprint;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    newFlightFootprint = footprint.getFlight() - d;
                    db.child(uid).child("footprint").child("flight").setValue(round(newFlightFootprint,2));
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
