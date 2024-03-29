package com.example.fyp.FlightFolder;

import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fyp.Footprint;
import com.example.fyp.LatLngMap;
import com.example.fyp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlightFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private TextView flightFootprint, logFlightsTextView;
    private EditText depart, arrival;
    private Button cancelFlightButton, addFlightButton, saveFlightButton;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase,fDatabase;
    private FirebaseUser firebaseUser;
    private String userID, classString, returnString;
    private boolean returnFlight, calculationValid;
    private double footprintInTonnes;
    private AutoCompleteTextView autoDepart, autoArrive;
    private String[] airports;
    private List<Flight> flightList;
    private RecyclerView recyclerView;
    private Spinner classSpinner, returnSpinner;
    private ArrayAdapter<CharSequence> classAdapter, returnAdapter;
    private LinearLayout addFlightLayout,recyclerLayout,totalLayout;
    private Boolean valid=false;
    private LatLngMap latLngMap = new LatLngMap();
    private Map locationMap;
    private Location arriveLocation,departLocation;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flight, container, false);
       // flightList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        fDatabase = FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("flights");
        fDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               flightList = new ArrayList<>();
               double total=0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Flight flight = dataSnapshot.getValue(Flight.class);
                    flightList.add(flight);
                    total+= flight.getFootprint();

                }

                flightFootprint.setText(String.valueOf(df.format(total) ) + " tonnes of CO2");
                recyclerView = view.findViewById(R.id.flights_recycler_view);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
                recyclerView.setAdapter(new FlightAdapter(view.getContext(), flightList));
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        depart = view.findViewById(R.id.editTextDepart);
        arrival = view.findViewById(R.id.editTextArrive);
        autoArrive = view.findViewById(R.id.autoCompleteArrive);
        autoDepart = view.findViewById(R.id.autoCompleteDepart);
        cancelFlightButton = view.findViewById(R.id.cancelFlightButton);
        addFlightButton = view.findViewById(R.id.openAddFlightButton);
        saveFlightButton = view.findViewById(R.id.saveFlightButton);
        flightFootprint = view.findViewById(R.id.flightFootprint);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        userID = firebaseUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        airports = getResources().getStringArray(R.array.airports);
        addFlightLayout= view.findViewById(R.id.addFlightLayout);
        recyclerLayout= view.findViewById(R.id.recyclerLayout);
        totalLayout= view.findViewById(R.id.totalFlightLayout);
        locationMap = latLngMap.getLocationMap();
        logFlightsTextView= view.findViewById(R.id.logFlights);


        userHasFlights();//checks if the user has any flights saved before setting the recyclerview visible

        setTotalFootprint();

        classSpinner = view.findViewById(R.id.class_spinner);
        classAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.class_spinner_options, android.R.layout.simple_spinner_item);
        classAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        classSpinner.setAdapter(classAdapter);
        classSpinner.setOnItemSelectedListener(this);

        returnSpinner = view.findViewById(R.id.return_spinner);
        returnAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.yes_no_spinner_options, android.R.layout.simple_spinner_item);
        returnAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        returnSpinner.setAdapter(returnAdapter);
        returnSpinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> airportAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, airports);
        autoArrive.setAdapter(airportAdapter);
        autoDepart.setAdapter(airportAdapter);


        AutoCompleteTextView.OnDismissListener dismissListener = new AutoCompleteTextView.OnDismissListener() {// closes the keyboard after an option has been selected from the drop down
            @Override
            public void onDismiss() {
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        };
        autoDepart.setOnDismissListener(dismissListener);
        autoArrive.setOnDismissListener(dismissListener);



        saveFlightButton.setOnClickListener(new View.OnClickListener() {
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

                   getFlight(bdepart,barrive);

                }

            }

        });

        cancelFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFlightLayout.setVisibility(View.GONE);
                addFlightButton.setVisibility(View.VISIBLE);
            }
        });

        addFlightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              addFlightLayout.setVisibility(View.VISIBLE);
              addFlightButton.setVisibility(View.GONE);
              logFlightsTextView.setVisibility(View.GONE);
            }
        });


    }

    private void getFlight(String depart,String arrive) {
        Thread thread = new Thread(new Runnable() {// create a new thread to place the api call inside to prevent async errors
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    //set up api call
                    URL url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=" + getIATA(depart) + "&segments[0][destination]=" + getIATA(arrive) + "&cabin_class=" + getFlightClass());
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization",
                            "Basic " + Base64.getEncoder().encodeToString((getString(R.string.go_climate_api_key) + ":").getBytes() //the api key for GoClimate
                            )
                    );
                    //handle api response
                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 200) {
                       // calculationValid = true;
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                        double jsonFootprint = jsonResponse.getInt("footprint");
                        footprintInTonnes = jsonFootprint / 1000;
                        if (returnFlight) {
                            footprintInTonnes = footprintInTonnes * 2;
                        }

                        //save the flight to the db
                        final String flightID = UUID.randomUUID().toString();//create a unique id for a flight
                        Flight flight = new Flight(autoArrive.getText().toString().trim(),autoDepart.getText().toString().trim(), classSpinner.getSelectedItem().toString(), flightID,getDistance(getIATA(depart),getIATA(arrive)), footprintInTonnes, getReturnFlight());
                        mDatabase.child(firebaseAuth.getUid()).child("flights").child(flightID).setValue(flight);
                        updateUserTotalFootprint(flight.getFootprint());
                        valid=true;

                        userHasFlights();

                    } else {

                        Log.e("URL : ", String.valueOf(url));
                        Log.e("Resonse code: ", String.valueOf(conn.getResponseCode()));
                        Log.e("Resonse message: ", String.valueOf(conn.getResponseMessage()));
                        valid=false;
                    }
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() { // used to access ui thread so view can be updated

                            @Override
                            public void run() {
                                if(valid){
                                    Toast.makeText(getContext(), "Flight Saved", Toast.LENGTH_SHORT).show();
                                    addFlightLayout.setVisibility(View.GONE);
                                    addFlightButton.setVisibility(View.VISIBLE);
                                    autoDepart.setText("");
                                    autoArrive.setText("");
                                    classSpinner.setSelection(0);
                                    returnSpinner.setSelection(0);
                                    valid=false;
                                }else{
                                    Toast.makeText(getContext(), "Invalid parameters", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }


                    conn.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }

        });
        thread.start();


    }

    private String getDistance(String departIATA, String arriveIATA) {
        String haul ="";
        arriveLocation= (Location) locationMap.get(arriveIATA);
        departLocation = (Location) locationMap.get(departIATA);

       double distanceKM= (departLocation.distanceTo(arriveLocation))/1000;
        Log.e("distance : ", String.valueOf(distanceKM));

       if(distanceKM<=1500){
           haul = "Short";
       }else if(distanceKM>=4100){
            haul = "Long";
        }else{
           haul = "Medium";
       }

       return haul;
    }

    private void userHasFlights() {
        FirebaseDatabase.getInstance().getReference(firebaseAuth.getUid()).child("flights").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //checks theres a flight in the database
                    if(snapshot.hasChildren()){
                        recyclerLayout.setVisibility(View.VISIBLE);


                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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
        mDatabase.child(firebaseAuth.getUid()).child("footprint").addListenerForSingleValueEvent(new ValueEventListener() {
            double newFlightFootprint;

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    newFlightFootprint = footprint.getFlight() + d;
                    mDatabase.child(firebaseAuth.getUid()).child("footprint").child("flight").setValue(newFlightFootprint);
                    totalLayout.setVisibility(View.VISIBLE);
                    flightFootprint.setText(String.valueOf(round(newFlightFootprint,2) ) + " tonnes of CO2");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void setTotalFootprint() {
        mDatabase.child(firebaseAuth.getUid()).child("footprint").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    totalLayout.setVisibility(View.VISIBLE);
                    flightFootprint.setText(String.valueOf(df.format(footprint.getFlight())) + " tonnes of CO2");
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
    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
