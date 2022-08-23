package com.example.fyp.FoodFolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class FoodFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private String meatString="",dairyString="", compostString="", organicString="",shoppingString="";
    private TextView displayTextView,meatTextView,dairyTextView, compostTextView, organicTextView,shoppingTextView;
    private Spinner meatSpinner,dairySpinner, compostSpinner, organicSpinner,shoppingSpinner;
    private double foodValue, meatValue,dairyValue, compostValue, organicValue,shoppingValue;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private String userID;
    private Button calculateButton;
    private LinearLayout totalLayout;
    private static final DecimalFormat df = new DecimalFormat("0.00");


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getUid();
        mDatabase.child(userID).child("food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Food food = snapshot.getValue(Food.class);
                    //set spinner values to users current saved inputs
                    int spinnerPosition;
                    ArrayAdapter<CharSequence> meatAdapter = ArrayAdapter.createFromResource(getContext(), R.array.meat_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = meatAdapter.getPosition(food.getMeatString());
                    meatSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> dairyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dairy_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = dairyAdapter.getPosition(food.getDairyString());
                    dairySpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> compostAdapter = ArrayAdapter.createFromResource(getContext(), R.array.yes_no_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = compostAdapter.getPosition(food.getCompostString());
                    compostSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> organicAdapter = ArrayAdapter.createFromResource(getContext(), R.array.organic_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = organicAdapter.getPosition(food.getOrganicString());
                    organicSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> shoppingAdapter = ArrayAdapter.createFromResource(getContext(), R.array.shopping_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = shoppingAdapter.getPosition(food.getShoppingString());
                    shoppingSpinner.setSelection(spinnerPosition);


                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
        return inflater.inflate(R.layout.fragment_food,container,false);


    }

    private void setValues() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child(userID).child("food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Food food = snapshot.getValue(Food.class);
                    //set spinner values to users current saved inputs
                    int spinnerPosition;
                    ArrayAdapter<CharSequence> meatAdapter = ArrayAdapter.createFromResource(getContext(), R.array.meat_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = meatAdapter.getPosition(food.getMeatString());
                    meatSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> dairyAdapter = ArrayAdapter.createFromResource(getContext(), R.array.dairy_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = dairyAdapter.getPosition(food.getDairyString());
                    dairySpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> compostAdapter = ArrayAdapter.createFromResource(getContext(), R.array.yes_no_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = compostAdapter.getPosition(food.getCompostString());
                    compostSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> organicAdapter = ArrayAdapter.createFromResource(getContext(), R.array.organic_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = organicAdapter.getPosition(food.getOrganicString());
                    organicSpinner.setSelection(spinnerPosition);

                    ArrayAdapter<CharSequence> shoppingAdapter = ArrayAdapter.createFromResource(getContext(), R.array.shopping_spinner_options, android.R.layout.simple_spinner_item);
                    spinnerPosition = shoppingAdapter.getPosition(food.getShoppingString());
                    shoppingSpinner.setSelection(spinnerPosition);


                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //link firebase
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        meatTextView= view.findViewById(R.id.editTextMeat);
        dairyTextView= view.findViewById(R.id.editTextDairy);
        compostTextView= view.findViewById(R.id.editTextCompost);
        organicTextView= view.findViewById(R.id.editTextOrganic);
        shoppingTextView= view.findViewById(R.id.editTextCompost);
        totalLayout = view.findViewById((R.id.totalFoodLayout));

        meatSpinner = view.findViewById(R.id.meat_spinner);
        ArrayAdapter<CharSequence> meatAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.meat_spinner_options, android.R.layout.simple_spinner_item);
        meatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        meatSpinner.setAdapter(meatAdapter);
        meatSpinner.setOnItemSelectedListener(this);

        dairySpinner = view.findViewById(R.id.dairy_spinner);
        ArrayAdapter<CharSequence> dairyAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.dairy_spinner_options, android.R.layout.simple_spinner_item);
        dairyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        dairySpinner.setAdapter(dairyAdapter);
        dairySpinner.setOnItemSelectedListener(this);

        compostSpinner = view.findViewById(R.id.compost_spinner);
        ArrayAdapter<CharSequence> compostAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.yes_no_spinner_options, android.R.layout.simple_spinner_item);
        compostAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        compostSpinner.setAdapter(compostAdapter);
        compostSpinner.setOnItemSelectedListener(this);

        organicSpinner = view.findViewById(R.id.organic_spinner);
        ArrayAdapter<CharSequence> organicAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.organic_spinner_options, android.R.layout.simple_spinner_item);
        organicAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        organicSpinner.setAdapter(organicAdapter);
        organicSpinner.setOnItemSelectedListener(this);

        shoppingSpinner = view.findViewById(R.id.shopping_spinner);
        ArrayAdapter<CharSequence> shoppingAdapter = ArrayAdapter.createFromResource(this.getContext(), R.array.shopping_spinner_options, android.R.layout.simple_spinner_item);
        shoppingAdapter.setDropDownViewResource(R.layout.custom_spinner);
        shoppingSpinner.setAdapter(shoppingAdapter);
        shoppingSpinner.setOnItemSelectedListener(this);

        displayTextView =  view.findViewById(R.id.textViewDisplay);

        calculateButton = view.findViewById(R.id.calculateFoodButton);
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateDisplay();
                mDatabase.child(firebaseAuth.getUid()).child("footprint").child("food").setValue(getFoodCO2());
                // to do only update this data when user wants to save input ie allow them to check the value based on their input into the spinners before they save it to firebase
                Food food = new Food(meatString,dairyString, compostString, organicString,shoppingString,meatValue,dairyValue,compostValue,organicValue,shoppingValue,foodValue);
                mDatabase.child(firebaseAuth.getUid()).child("food").setValue(food);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //set the value from the spinner
        switch(adapterView.getId()){
            case R.id.meat_spinner:
                meatString = adapterView.getItemAtPosition(i).toString();
                updateDisplay();
                break;
            case R.id.dairy_spinner:
                dairyString = adapterView.getItemAtPosition(i).toString();
                updateDisplay();
                break;
            case R.id.compost_spinner:
                compostString = adapterView.getItemAtPosition(i).toString();
                updateDisplay();
                break;
            case R.id.organic_spinner:
                organicString = adapterView.getItemAtPosition(i).toString();
                updateDisplay();
                break;
            case R.id.shopping_spinner:
                updateDisplay();
                break;
        }
       // getFoodCO2();
       // updateDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }




    public double getFoodCO2() {

        getMeatCO2();
        getDairyCO2();
        getCompostCO2();
        getOrganicCO2();
        getShoppingCO2();

        //foodValue=value;
        return meatValue+dairyValue+compostValue+organicValue+shoppingValue;
    }

    public double getMeatCO2() {

        double value = 0;
        switch (meatString) {
            case"":
                break;
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
        meatValue= value;
        return meatValue;
    }

    public double getDairyCO2() {

        double value = 0;
        switch(dairyString) {
            case"":
                break;
            case "Never":
                break;
            case "Occasionally":
                value = value + .04;
                break;
            case "Every day":
                value = value + .07;
                break;
        }

        dairyValue= value;
        return dairyValue;
    }

    public double getCompostCO2() {

        double value = 0;
        switch(compostString) {
            case"":
                break;
            case "Yes":
                break;
            case "No":
                value = value + .03;
                break;
        }
        compostValue= value;
        return compostValue;
    }

    public double getShoppingCO2() {

        double value = 0;
        switch(shoppingString) {
            case"":
                break;
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
        shoppingValue= value;
        return shoppingValue;
    }

    public double getOrganicCO2() {

        double value = 0;
        switch(organicString) {
            case"":
                break;
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
        organicValue= value;
        return organicValue;
    }


    public void updateDisplay(){
        totalLayout.setVisibility(View.VISIBLE);
        foodValue=round(getFoodCO2(),2);
        displayTextView.setText( foodValue  + " Tonnes of CO2");
    };

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


}
