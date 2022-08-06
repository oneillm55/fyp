package com.example.fyp.FlightFolder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fyp.MainActivity;
import com.example.fyp.R;
import com.example.fyp.UserFolder.User;
import com.example.fyp.UserFolder.UserDetailsActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;


public class FlightActivity extends AppCompatActivity {

    private EditText depart, arrival;
    private Button calculateFlightButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String userID;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
        depart = findViewById(R.id.editTextDepart);
        arrival = findViewById(R.id.editTextArrive);
        calculateFlightButton = findViewById(R.id.calculateFlightButton);
      //  firebaseAuth = FirebaseAuth.getInstance();
//        userID = firebaseUser.getUid();

        calculateFlightButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String bdepart = depart.getText().toString().trim();
                String barrival = arrival.getText().toString().trim();



                if (barrival.isEmpty() || bdepart.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please insure no fields are left blank", Toast.LENGTH_SHORT).show();
                } else {

                    Thread thread = new Thread(new Runnable() {// had to place the api call inside its own thread to prevent async errors

                        @Override
                        public void run() {
                            try  {
                                String username = "311e02419b9ceb0523ef1a5f";
                                String password = "";
// ...
                                URL url = null;
                                try {
                                    url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    conn.setRequestMethod("GET");
                                    conn.setRequestProperty("Accept", "application/json");
// snippet begins
                                    conn.setRequestProperty("Authorization",
                                            "Basic " + Base64.getEncoder().encodeToString(
                                                    (username + ":" + password).getBytes()
                                            )
                                    );
// snippet ends
                                    // System.out.println("Output is: "+conn.getResponseCode());
                //                    Toast.makeText(getApplicationContext(), "Response code !!! ",conn.getResponseCode());
                                    Log.e("!!! res code !!! ", String.valueOf(conn.getResponseCode()));
                                    Log.e("!!! res message !!! ", conn.getResponseMessage());
                                    int status = conn.getResponseCode();

                                    switch (status) {
                                        case 200:
                                        case 201:
                                            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                            StringBuilder sb = new StringBuilder();
                                            String line;
                                            while ((line = br.readLine()) != null) {
                                                sb.append(line+"\n");
                                            }
                                            br.close();
                                        Log.e("!!! sb.toString(); !!! ", sb.toString());
                                    }

                                    conn.disconnect();
                                } catch (MalformedURLException e) {
                                    e.printStackTrace();
                                } catch (ProtocolException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    thread.start();



                    //send the values from fields to api and calculate visualisation from results
//                    try {
//                        URL url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
//                       // url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]="+"ARN"+"&segments[0][destination]="+"BCN"+"&segments[1][origin]="+"BCN"+"&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
//
////                    URLConnection uc = url.openConnection();
//                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
//                        String userpass = "311e02419b9ceb0523ef1a5f" + ":";
//                        //  String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
//                        String encodedBytes = Base64.getEncoder().encodeToString((userpass ).getBytes());
//                    con.setRequestProperty("Authorization", "Basic " + encodedBytes);
//                        con.setRequestMethod("GET");
//                    //    String authHeaderValue = "Basic " + encodedBytes;
////                    uc.setRequestProperty ("Authorization", authHeaderValue);
////                    InputStream in = uc.getInputStream();
////                    Log.e("stream!!!!",in.toString());
//                        int status = con.getResponseCode();
//
//                        if (status == 200){
//                            Log.e("status!!!","200");
//
//                        }
//
//                        con.disconnect();
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }


                    //add flight to database
//                    Flight flight = new Flight(barrival,bdepart,"10",userID);
//
//                    mDatabase.child("trips").child(userID).setValue(flight);


//                    //launch fragment instead of activity
//                    Intent i = new Intent(FlightActivity.this, FlightDisplayActivity.class);
//                    i.putExtra("depart", bdepart);
//                    i.putExtra("arrive", barrival);
//                    // i.putExtra("amount", amount);
//                    startActivity(i);
                }
            }

        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(FlightActivity.this, MainActivity.class));
        } else if (item.getItemId() == R.id.user_details) {

            startActivity(new Intent(FlightActivity.this, UserDetailsActivity.class));

        }


        return super.onOptionsItemSelected(item);
    }
}