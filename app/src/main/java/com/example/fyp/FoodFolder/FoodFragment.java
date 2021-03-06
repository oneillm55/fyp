package com.example.fyp.FoodFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
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

import com.example.fyp.AppDrawer.DrawerActivity;
import com.example.fyp.R;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;

public class FoodFragment extends Fragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    String meatString,dairyString, compostString, organicString,shoppingString;
    TextView displayTextView;
    double foodValue;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String userID;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_food,container,false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //link firebase
        firebaseAuth = FirebaseAuth.getInstance();


        mDatabase = FirebaseDatabase.getInstance().getReference();




        Spinner meatSpinner = view.findViewById(R.id.meat_spinner);
        ArrayAdapter<CharSequence> meatAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.meat_spinner_options, android.R.layout.simple_spinner_item);
        meatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        meatSpinner.setAdapter(meatAdapter);
        meatSpinner.setOnItemSelectedListener(this);

        Spinner dairySpinner = view.findViewById(R.id.dairy_spinner);
        ArrayAdapter<CharSequence> dairyAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dairy_spinner_options, android.R.layout.simple_spinner_item);
        dairyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dairySpinner.setAdapter(dairyAdapter);
        dairySpinner.setOnItemSelectedListener(this);

        Spinner compostSpinner = view.findViewById(R.id.compost_spinner);
        ArrayAdapter<CharSequence> compostAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.compost_spinner_options, android.R.layout.simple_spinner_item);
        compostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        compostSpinner.setAdapter(compostAdapter);
        compostSpinner.setOnItemSelectedListener(this);

        Spinner organicSpinner = view.findViewById(R.id.organic_spinner);
        ArrayAdapter<CharSequence> organicAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.organic_spinner_options, android.R.layout.simple_spinner_item);
        organicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        organicSpinner.setAdapter(organicAdapter);
        organicSpinner.setOnItemSelectedListener(this);

        Spinner shoppingSpinner = view.findViewById(R.id.shopping_spinner);
        ArrayAdapter<CharSequence> shoppingAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.shopping_spinner_options, android.R.layout.simple_spinner_item);
        shoppingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        shoppingSpinner.setAdapter(shoppingAdapter);
        shoppingSpinner.setOnItemSelectedListener(this);

        displayTextView =  view.findViewById(R.id.textViewDisplay);

        Button calculateButton = view.findViewById(R.id.calculateFoodButton);
        calculateButton.setOnClickListener(this);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //set the value from the spinner
        switch(adapterView.getId()){
            case R.id.meat_spinner:
                meatString = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.dairy_spinner:
                dairyString = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.compost_spinner:
                compostString = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.organic_spinner:
                organicString = adapterView.getItemAtPosition(i).toString();
                break;
            case R.id.shopping_spinner:
                shoppingString = adapterView.getItemAtPosition(i).toString();
                break;
        }
       // getFoodCO2();
       // updateDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    @Override
    public void onClick(View view) {
        updateDisplay();
        mDatabase.child("footprint").child(firebaseAuth.getUid()).child("food").setValue(getFoodCO2());
        // to do only update this data when user wants to save input ie allow them to check the value based on their input into the spinners before they save it to firebase
    }



    public double getFoodCO2() {

        double value=0;
        switch(meatString) {
            case "Never":
                break;
            case "Occasionally":
                value = value + .08;
                break;
            case "Once a day":
                value = value + .21;
                break;
            case "Most meals":
                value = value + .33;
                break;
        }
        switch(dairyString) {
            case "Never":
                break;
            case "Occasionally":
                value = value + .04;
                break;
            case "Everyday":
                value = value + .07;
                break;
        }

        switch(compostString) {
            case "Yes":
                break;
            case "No":
                value = value + .03;
                break;
        }

        switch(organicString) {
            case "None":
                value = value + .06;
                break;
            case "Some":
                value = value + .03;
                break;
            case "Most":
                value = value + .03; //to do find accurate number
                break;
        }

        switch(shoppingString) {
            case "Only local produce":
                value = value + .03;//to do find accurate number
                break;
            case "Mostly Local, some supermarkets":
                value = value + .07;
                break;
            case "Mostly supermarkets, try to buy Irish":
                value = value + .15;
                break;
            case "All supermarkets, pay no attention to country of origin":
                value = value + .21;
                break;
        }

        //foodValue=value;
        return value;
    }

    public void updateDisplay(){
        displayTextView.setText( df.format(getFoodCO2()) + " tonnes of CO2");
    };


}
