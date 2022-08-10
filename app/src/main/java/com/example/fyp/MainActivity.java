package com.example.fyp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fyp.AppDrawer.DrawerActivity;
import com.example.fyp.RegistrationFolder.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginButton, signupButton;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        signupButton = findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bemail = email.getText().toString().trim();
                String bpassword = password.getText().toString().trim();

                if (bpassword.isEmpty() || bemail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please insure username and password are filled out", Toast.LENGTH_SHORT).show();
                } else {
                    //login user
                    firebaseAuth.signInWithEmailAndPassword(bemail,bpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                // checkVerification();
                                startActivity(new Intent(MainActivity.this, DrawerActivity.class));

                            }else{
                                Toast.makeText(getApplicationContext(), "Username and/or password not recognised", Toast.LENGTH_SHORT).show();

                            }

                        }
                    });
                }
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //call signup activity
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }

            ;
        });


    }

    private void checkVerification() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser.isEmailVerified()){
            //login
            //finish();
            startActivity(new Intent(MainActivity.this, DrawerActivity.class));
        }else{
            Toast.makeText(getApplicationContext(), "Please verify your email first", Toast.LENGTH_SHORT).show();

        }
    }
}