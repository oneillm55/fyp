package com.example.fyp.FlightFolder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.FlightFolder.Flight;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFlightsFragment extends Fragment {

    private TextView depart, arrival, score;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;
    FlightAdapter flightAdapter;
    List<Flight> flightList;
    RecyclerView recyclerView;
    private FlightAdapter.recyclerOnClickListener listener;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_flights, container, false);
        flightList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        //firebaseUser = firebaseAuth.getCurrentUser();
        //  userID = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("flights").child(firebaseAuth.getUid());
        Log.e("mdatabase", mDatabase.toString());

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flight flight = dataSnapshot.getValue(Flight.class);
                    flightList.add(flight);
                }

                recyclerView = view.findViewById(R.id.flights_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new FlightAdapter(view.getContext(), flightList));
            }

//            private void setOnClickListener() {
//                listener = new FlightAdapter.recyclerOnClickListener() {
//                    @Override
//                    public void onClick(View v, int position) {
//                        Toast.makeText(getContext(), "Click", Toast.LENGTH_SHORT).show();
//                        //add to cart
//
//                        new AlertDialog.Builder(getContext())
//                                .setTitle("Delete Flight")
//                                .setMessage("Are you sure you want to delete this flight?")
//
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int id) {
//                                        //delete flight
//
//                                        //mDatabase.drop(flightID);
//                                    }
//                                })
//
//                                .setNegativeButton(android.R.string.no, null)
//                                .setIcon(android.R.drawable.ic_dialog_alert)
//                                .show();
//                    }
//                };
//            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }
}





