package com.example.fyp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDetailsActivity extends AppCompatActivity  {

    Intent userData;
    TextView userEmail;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    //FirebaseFirestore firebaseFirestore;
    FirebaseUser firebaseUser;
    String userId, emailString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        getSupportActionBar().setTitle("User Details");

        userEmail= findViewById(R.id.view_user_email);
        userData=getIntent();

       // firebaseFirestore=FirebaseFirestore.getInstance();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        firebaseAuth=FirebaseAuth.getInstance();


        userId = firebaseAuth.getCurrentUser().getUid();
        emailString= firebaseAuth.getCurrentUser().getEmail();
        userEmail.setText("Current user email:\n "+emailString);

        mDatabase.child("users").child(userId).child(userId).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    //userEmail.setText("Current user email:\n "+value.getString("email"));
                }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}