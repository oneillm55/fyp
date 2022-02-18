package com.example.fyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class FlightActivity extends AppCompatActivity {

    private EditText depart, arrival;
    private Button calculateFlightButton;
    private FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

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
        firebaseAuth = FirebaseAuth.getInstance();

        calculateFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bdepart = depart.getText().toString().trim();
                String barrival = arrival.getText().toString().trim();

                if (barrival.isEmpty() || bdepart.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please insure no fields are left blank", Toast.LENGTH_SHORT).show();
                } else {
                    //send the values from fields to api and calculate visualisation from results
//                    URL url = null;
//                    try {
//                        url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=ARN&segments[0][destination]=BCN&segments[1][origin]=BCN&segments[1][destination]=ARN&cabin_class=economy&currencies[]=SEK&currencies[]=USD");
//
//                    URLConnection uc = url.openConnection();
//
//                    String userpass = "311e02419b9ceb0523ef1a5f" + ":";
//                    String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
//
//                    uc.setRequestProperty ("Authorization", basicAuth);
//                    InputStream in = uc.getInputStream();
//                    Log.e("stream!!!!",in.toString());
//                    } catch (MalformedURLException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    Intent i = new Intent(FlightActivity.this, FlightDisplayActivity.class);
                    i.putExtra("depart", bdepart);
                    i.putExtra("arrive", barrival);
                    // i.putExtra("amount", amount);
                    startActivity(i);
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