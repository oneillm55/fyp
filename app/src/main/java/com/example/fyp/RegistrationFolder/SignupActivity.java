package com.example.fyp.RegistrationFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fyp.MainActivity;
import com.example.fyp.R;
import com.example.fyp.UserFolder.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    private EditText signupUsername,signupEmail,signupPassword,signupConfirmPassword;
    private Button registerButton,goBackButton;


    private FirebaseAuth firebaseAuth;
    private String userID;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        signupUsername=findViewById(R.id.editTextUsernameRegister);
        signupEmail=findViewById(R.id.editTextEmailRegister);
        signupPassword=findViewById(R.id.editTextPasswordRegister);
        signupConfirmPassword=findViewById(R.id.editTextConfirmPasswordRegister);
        registerButton =findViewById(R.id.buttonRegister);
        goBackButton=findViewById(R.id.buttonReturnLogin);

        //getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference();

        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, MainActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String busername=signupUsername.getText().toString().trim();
                String bemail=signupEmail.getText().toString().trim();
                String bpassword = signupPassword.getText().toString().trim();
                String bconfirmpassword = signupConfirmPassword.getText().toString().trim();



                if(bpassword.isEmpty() || bemail.isEmpty() || busername.isEmpty() || bconfirmpassword.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please insure all fields are filled out", Toast.LENGTH_SHORT).show();
                }else if(bpassword.length()<7){
                    Toast.makeText(getApplicationContext(), "Password must be at least 7 characters", Toast.LENGTH_SHORT).show();

                }else if(!bpassword.equals(bconfirmpassword)){
                    Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();

                }else{


                    //register user
                    firebaseAuth.createUserWithEmailAndPassword(bemail,bpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Registration Complete", Toast.LENGTH_SHORT).show();

                                //realtime
                                 User user = new User(busername,bemail,bpassword);

                                 mDatabase.child("users").child(userID).setValue(user);


                                startActivity(new Intent(SignupActivity.this,MainActivity.class));

                            }else{
                                Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                Log.e("error", "onComplete: Failed=" + task.getException().getMessage());

                            }

                        }
                    });
                }
            }
        });
    }

    private void sendVerificationEmail() {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null){
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification Email Sent", Toast.LENGTH_SHORT).show();
                    firebaseAuth.signOut();
                    finish();
                    //launch login again
                }
            });
        }else{
            Toast.makeText(getApplicationContext(), "Verification Email Failed", Toast.LENGTH_SHORT).show();


        }
    }
}