package com.example.fyp.FlightFolder;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fyp.Footprint;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class FlightFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView flightFootprint;
    private EditText depart, arrival;
    private Button calculateFlightButton, viewFlightButton, saveFlightButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private String userID, classString, returnString;
    private boolean returnFlight, calculationValid;
    private double footprintInTonnes;
    private AutoCompleteTextView autoDepart, autoArrive;
    private String[] airports;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_flight, container, false);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depart = view.findViewById(R.id.editTextDepart);
        arrival = view.findViewById(R.id.editTextArrive);
        autoArrive = view.findViewById(R.id.autoCompleteArrive);
        autoDepart = view.findViewById(R.id.autoCompleteDepart);
        calculateFlightButton = view.findViewById(R.id.calculateFlightButton);
        viewFlightButton = view.findViewById(R.id.viewFlightButton);
        saveFlightButton = view.findViewById(R.id.saveFlightButton);
        flightFootprint = view.findViewById(R.id.flightFootprint);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        calculationValid = false;
        airports = getResources().getStringArray(R.array.airports);

        Spinner classSpinner = view.findViewById(R.id.class_spinner);
        ArrayAdapter<CharSequence> classAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.class_spinner_options, android.R.layout.simple_spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        classSpinner.setAdapter(classAdapter);
        classSpinner.setOnItemSelectedListener(this);

        Spinner returnSpinner = view.findViewById(R.id.return_spinner);
        ArrayAdapter<CharSequence> returnAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.yes_no_spinner_options, android.R.layout.simple_spinner_item);
        returnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        returnSpinner.setAdapter(returnAdapter);
        returnSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> airportAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, airports);
        autoArrive.setAdapter(airportAdapter);
        autoDepart.setAdapter(airportAdapter);

        calculateFlightButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                String bdepart = autoDepart.getText().toString().trim();
                String barrive = autoArrive.getText().toString().trim();
                classString = classSpinner.getSelectedItem().toString();
                returnFlight = getReturnFlight();


                if (barrive.isEmpty() || bdepart.isEmpty()) {
                    Toast.makeText(getContext(), "Please insure no fields are left blank", Toast.LENGTH_SHORT).show();
                } else if (!validAirport(barrive) || !validAirport(bdepart)) {
                    Toast.makeText(getContext(), "Please use suggested inputs only", Toast.LENGTH_SHORT).show();
                } else {
                    Thread thread = new Thread(new Runnable() {// create a new thread to place the api call inside to prevent async errors

                        @Override
                        public void run() {
                            try {

                                URL url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=" + getIATA(bdepart) + "&segments[0][destination]=" + getIATA(barrive) + "&cabin_class=" + getFlightClass());
                                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                conn.setRequestMethod("GET");
                                conn.setRequestProperty("Accept", "application/json");
                                conn.setRequestProperty("Authorization",
                                        "Basic " + Base64.getEncoder().encodeToString((getString(R.string.go_climate_api_key) + ":").getBytes() //the api key for GoClimate
                                        )
                                );
                                if (conn.getResponseCode() == 200 || conn.getResponseCode() == 200) {
                                    calculationValid = true;
                                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                                    StringBuilder stringBuilder = new StringBuilder();
                                    String line;
                                    while ((line = reader.readLine()) != null) {
                                        stringBuilder.append(line);
                                    }
                                  //  Log.e("!!!: ", stringBuilder.toString());

                                    JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                                    double jsonFootprint = jsonResponse.getInt("footprint");
                                    footprintInTonnes = jsonFootprint / 1000;
                                    if (returnFlight) {
                                        footprintInTonnes = footprintInTonnes * 2;
                                    }

                                    flightFootprint.setText(String.valueOf(footprintInTonnes) + " tonnes of CO2");
                                    getActivity().runOnUiThread(new Runnable() { // used to access ui thread so view can be updated

                                        @Override
                                        public void run() {

                                            saveFlightButton.setVisibility(View.VISIBLE);

                                        }
                                    });

                                } else {
                                    getActivity().runOnUiThread(new Runnable() { // used to access ui thread so view can be updated

                                        @Override
                                        public void run() {
                                            Toast.makeText(getContext(), "Invalid parameters", Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                    Log.e("URL : ", String.valueOf(url));
                                    Log.e("Resonse code: ", String.valueOf(conn.getResponseCode()));
                                    Log.e("Resonse message: ", String.valueOf(conn.getResponseMessage()));
                                }

                                conn.disconnect();

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                }
            }

        });

        viewFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().beginTransaction().add(R.id.flight_fragment_container, new ViewFlightsFragment()).addToBackStack("fragBack").commit();
            }
        });

        saveFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //add check to make sure a flight has been calculated
                if(calculationValid){
                    final String flightID = UUID.randomUUID().toString();//create a unique id for a flight
                   Flight flight = new Flight(autoDepart.getText().toString().trim(), autoArrive.getText().toString().trim(), classSpinner.getSelectedItem().toString(), flightID, footprintInTonnes, getReturnFlight());
                    mDatabase.child("flights").child(firebaseAuth.getUid()).child(flightID).setValue(flight);
                    updateUserTotalFootprint(flight.getFootprint());
                    Toast.makeText(getContext(), "Flight Saved", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "Please calculate a flight first", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private boolean validAirport(String s) {

        if (Arrays.asList(airports).contains(s)) {
            return true;
        } else {
            return false;
        }
    }

    private String getIATA(String s) {
        return s.substring(s.length() - 4, s.length() - 1);
    }

    private void updateUserTotalFootprint(double d) {
        mDatabase.child("footprint").child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            double newFlightFootprint;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    newFlightFootprint = footprint.getFlight() + d;
                    mDatabase.child("footprint").child(firebaseAuth.getUid()).child("flight").setValue(newFlightFootprint);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private String getFlightClass() {
        String flightClass = "";

        switch (classString) {
            case "Economy":
                flightClass = "economy";
                break;
            case "Business":
                flightClass = "business";
                break;
            case "First":
                flightClass = "first";
                break;
        }

        return flightClass;
    }

    private boolean getReturnFlight() {
        boolean returnFlight = false;

        switch (returnString) {
            case "Yes":
                returnFlight = true;
                break;
            case "No":
                returnFlight = false;
                break;
        }

        return returnFlight;

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.class_spinner:
                classString = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.return_spinner:
                returnString = adapterView.getItemAtPosition(i).toString();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


}
