package com.example.fyp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ViewFlightsFragment extends Fragment {

    private TextView depart, arrival, score;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference mDatabase;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flight,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depart = view.findViewById(R.id.depart_name);
        arrival = view.findViewById(R.id.arrive_name);
        score = view.findViewById(R.id.flight_score);
        firebaseAuth = FirebaseAuth.getInstance();



    }

    public class LocationViewHolder extends RecyclerView.ViewHolder{

        TextView locationTitle,locationWeather,locationTemp,locationTime;
        LinearLayout visit;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);



        }
    }
}
