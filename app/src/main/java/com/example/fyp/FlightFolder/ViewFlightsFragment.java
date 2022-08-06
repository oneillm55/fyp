package com.example.fyp.FlightFolder;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_view_flights,container,false);
        flightList = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference("trips");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    Flight flight = dataSnapshot.getValue(Flight.class);
//                    if(flight.getUuid().equalsIgnoreCase(firebaseUser.getUid())){
//                        flightList.add(flight);
//                    };
//                    flightList.add(flight);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
      //  flightList.add(new Flight("a","b","10","123"));
        recyclerView = view.findViewById(R.id.flights_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        recyclerView.setAdapter(new FlightAdapter(view.getContext(), flightList));

    return view;
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        depart = view.findViewById(R.id.depart_name);
//        arrival = view.findViewById(R.id.arrive_name);
//        score = view.findViewById(R.id.flight_score);
//        firebaseAuth = FirebaseAuth.getInstance();
//    }

    }



