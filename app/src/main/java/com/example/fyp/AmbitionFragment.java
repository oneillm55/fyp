package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AmbitionFragment extends Fragment {
    Button highButton, moderateButton, continueButton;
    String ambition="",userID;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        return inflater.inflate(R.layout.fragment_ambition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        highButton= view.findViewById(R.id.highButton);
        moderateButton = view.findViewById(R.id.moderateButton);
        continueButton = view.findViewById(R.id.continueButton);
        mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);

                    if(footprint.getAmbition().equalsIgnoreCase("High")){
                        highButton.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                        moderateButton.setBackgroundColor(Color.parseColor("#FF018786"));
                        ambition="High";
                    }else{
                        moderateButton.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                        highButton.setBackgroundColor(Color.parseColor("#FF018786"));
                        ambition="Moderate";
                    }
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        highButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highButton.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                moderateButton.setBackgroundColor(Color.parseColor("#FF018786"));
                ambition="High";
            }
        });
        moderateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moderateButton.setBackgroundColor(Color.parseColor("#FF03DAC5"));
                highButton.setBackgroundColor(Color.parseColor("#FF018786"));
                ambition="Moderate";
            }
        });
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ambition.equalsIgnoreCase("")){
                    Toast.makeText(getContext(), "Please select a value", Toast.LENGTH_SHORT).show();
                }else{
                    mDatabase.child(userID).child("footprint").child("ambition").setValue(ambition);
                    Toast.makeText(getContext(), "Input saved", Toast.LENGTH_SHORT).show();
                }
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}