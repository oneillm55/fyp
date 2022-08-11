package com.example.fyp.UserFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.MainActivity;
import com.example.fyp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserFragment extends Fragment {
   // Intent userData;
    TextView userEmail,username;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    String userId, emailString,usernameString;
    Button logoutButton;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        userEmail= view.findViewById(R.id.textViewEmail);
        username= view.findViewById(R.id.textViewUsername);
        logoutButton = view.findViewById(R.id.logoutButton);
        firebaseAuth = FirebaseAuth.getInstance();

        userEmail.setText("Email:\n "+emailString);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(firebaseAuth.getUid());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    userEmail.setText(user.getEmail());
                    username.setText(user.getUsernname());
                }
            }
                @Override
                public void onCancelled (@NonNull DatabaseError error){

                }
            });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                Intent intent = new Intent(getActivity(),MainActivity.class );
                startActivity(intent);
               // finish();
                //Toast.makeText(getActivity(), "Logout Successful")
            }
        });

         return view;
    }

    }
