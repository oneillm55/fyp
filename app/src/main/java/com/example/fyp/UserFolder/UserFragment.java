package com.example.fyp.UserFolder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.bumptech.glide.request.RequestOptions;
import com.example.fyp.GlideApp;
import com.example.fyp.MainActivity;
import com.example.fyp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class UserFragment extends Fragment {
   // Intent userData;
   public static final int REQUEST_CODE = 11;
    public static final int RESULT_CODE = 12;
    TextView userEmail,username, editPictureText;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    FirebaseUser firebaseUser;
    String userID, emailString,usernameString, imageKey;
    Button logoutButton, saveButton;
    ImageView profileImage;
    public Uri imageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    DatabaseReference databaseReference;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        userEmail= view.findViewById(R.id.textViewEmail);
        username= view.findViewById(R.id.textViewUsername);
        logoutButton = view.findViewById(R.id.logoutButton);
        saveButton = view.findViewById(R.id.saveButton);
        profileImage = view.findViewById(R.id.userProfilePicture);
        editPictureText = view.findViewById(R.id.editPictureText);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        userEmail.setText("Email:\n "+emailString);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference("users").child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    userEmail.setText(user.getEmail());
                    username.setText(user.getUsernname());
                    if (getActivity() == null) {//prevents crash from glide trying to use getactivity from the fragment being detached before getting a response from firebase
                        return;
                    }



                    if(user.getImageID().equalsIgnoreCase(" ")){

                        GlideApp.with(getActivity()).load(R.drawable.ic_baseline_person_outline_24).apply(new RequestOptions().override(200, 200)).into(profileImage);
                    }else {
                        GlideApp.with(getActivity()).load(storageReference.child("images").child(user.getImageID())).apply(new RequestOptions().override(200, 200)).into(profileImage);
                    }
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
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//prevents user from being able to go back to this page after log out using back button
                startActivity(intent);
               // finish();
                //Toast.makeText(getActivity(), "Logout Successful")
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseImage();


            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
                saveButton.setVisibility(View.GONE);
                editPictureText.setVisibility(View.VISIBLE);

            }
        });



         return view;
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("In on activity result", String.valueOf(requestCode) +String.valueOf(resultCode));
        if(requestCode==1 && resultCode==-1 && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            saveButton.setVisibility(View.VISIBLE);
            editPictureText.setVisibility(View.GONE);

        }
    }


    private void uploadImage() {
        final String randomKey = UUID.randomUUID().toString();
        imageKey = randomKey;
        StorageReference sr = storageReference.child("images/" + imageKey);


       sr.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Image Uploaded successfully", Toast.LENGTH_SHORT).show();
                        //set user imageChanged to current time stamp
                        updateImageChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void updateImageChanged() {
        //set the users ImageID varible to be the time the image was edited
        mDatabase.child("users").child(userID).child("imageID").setValue(imageKey);

    }


}
