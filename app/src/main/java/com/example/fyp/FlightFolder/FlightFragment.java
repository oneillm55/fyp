package com.example.fyp.FlightFolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class FlightFragment extends Fragment {

    private EditText depart, arrival;
    private Button calculateFlightButton,viewFlightButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private  FirebaseUser firebaseUser;
    private String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flight,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depart = view.findViewById(R.id.editTextDepart);
        arrival = view.findViewById(R.id.editTextArrive);
        calculateFlightButton = view.findViewById(R.id.calculateFlightButton);
        viewFlightButton = view.findViewById(R.id.viewFlightButton);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();

        calculateFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bdepart = depart.getText().toString().trim();
                String barrival = arrival.getText().toString().trim();

                if (barrival.isEmpty() || bdepart.isEmpty()) {
                    Toast.makeText(getContext(), "Please insure no fields are left blank", Toast.LENGTH_SHORT).show();
                } else {
                    //send the values from fields to api and calculate visualisation from results
                    String urlString = "https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD";

                    try {
                        URL url = new URL(urlString);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestProperty("Authorization", "Basic MzExZTAyNDE5YjljZWIwNTIzZWYxYTVmOg==");
                        urlConnection.setRequestMethod("GET");
                        if (urlConnection.getResponseCode() == 200) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                stringBuilder.append(line);
                            }
                            Log.e("!!!: ",stringBuilder.toString());

                            urlConnection.disconnect();
                        }
                    }catch(IOException e){
                            e.printStackTrace();
                    }

//                    try {
//                        url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
//
//                    HttpURLConnection uc = (HttpURLConnection) url.openConnection();
//                    uc.setRequestProperty ("Authorization", "Basic MzExZTAyNDE5YjljZWIwNTIzZWYxYTVmOg==");
//
//
////                    String userpass = "311e02419b9ceb0523ef1a5f" + ":";
////                  String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
////                    InputStream in = uc.getInputStream();
////                    Log.e("stream!!!!",in.toString());
//
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                        //add flight to database
//                    Flight flight = new Flight(barrival,bdepart,"10",userID);
//
//                    mDatabase.child("trips").setValue(flight);
//                    Intent i = new Intent(FlightFragment.this,
//                            FlightDisplayActivity.class);
//                    i.putExtra("depart", bdepart);
//                    i.putExtra("arrive", barrival);
                    // i.putExtra("amount", amount);
//                    startActivity(i);
                    }
                }

            });

        viewFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //launch view flights fragment

                getParentFragmentManager().beginTransaction().replace(R.id.flight_fragment_container, new ViewFlightsFragment())
                        .commit();



            }
        });

        }


    }
