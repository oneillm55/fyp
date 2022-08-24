package com.example.fyp.ClothingFolder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.UUID;

public class ClothingFragment extends Fragment {
    Slider airDrySlider, secondHandSlider, sustainableSlider, coldWashSlider,returnSlider,returnOnlineSlider;
    double airDryLbs, secondHandLbs, sustainableLbs, coldWashLbs, returnLbs,returnOnlineValue, totalLbs;
    boolean airDryEdited, secondHandEdited, sustainableEdited, coldWashEdited,returnEdited,returnOnlineEdited;// status of if a slider has been used yet in the app
    TextView clothesTotal, airDryTextView, secondHandTextView, sustainableTextView, coldWashTextView,returnTextView,returnOnlineTextView, buyingTextView;//these store the questions related to each slider
    Button button;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser firebaseUser;
    private String userID;
    private LinearLayout totalLayout;
    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //load slders to be at previously set values

        return inflater.inflate(R.layout.fragment_clothing, container, false);
    }

    @SuppressLint("ResourceType")//to allow use of ints as ids for spinners
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        airDrySlider = view.findViewById(R.id.airDrySlider);
        secondHandSlider = view.findViewById(R.id.secondHandSlider);
        sustainableSlider = view.findViewById(R.id.sustainableSlider);
        coldWashSlider = view.findViewById(R.id.coldWashSlider);
        returnSlider=view.findViewById(R.id.returnSlider);
        returnOnlineSlider=view.findViewById(R.id.returnOnlineSlider);
        button = view.findViewById(R.id.clothingButton);
        clothesTotal =  view.findViewById(R.id.clothesTotal);
        returnOnlineTextView= view.findViewById(R.id.returnOnlineTextView);
        secondHandTextView = view.findViewById(R.id.secondHandTextView);
        sustainableTextView= view.findViewById(R.id.sustainableTextView);
        coldWashTextView= view.findViewById(R.id.coldWashTextView);
        returnTextView= view.findViewById(R.id.returnTextView);
        returnOnlineTextView= view.findViewById(R.id.returnOnlineTextView);
        buyingTextView = view.findViewById(R.id.buyingTextView);
        airDryTextView = view.findViewById(R.id.airDryTextView);
        totalLayout = view.findViewById(R.id.totalClothingLayout);


        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //set ids so the app knows what order to set questions visible
        coldWashSlider.setId(1);
        airDrySlider.setId(2);
        secondHandSlider.setId(3);
        sustainableSlider.setId(4);
        returnSlider.setId(5);
        returnOnlineSlider.setId(6);

        //create instance of the slider touch listener and assign it to each slider
        MyOnSliderTouchListener listener = new MyOnSliderTouchListener();
        airDrySlider.addOnSliderTouchListener(listener);
        secondHandSlider.addOnSliderTouchListener(listener);
        sustainableSlider.addOnSliderTouchListener(listener);
        coldWashSlider.addOnSliderTouchListener(listener);
        returnSlider.addOnSliderTouchListener(listener);
        returnOnlineSlider.addOnSliderTouchListener(listener);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //totalLbs=round(calculateClothesTotal(),2);
               // clothesTotal.setText(String.valueOf(totalLbs+"lbs of CO2"));
                totalLbs=calculateClothesTotal();
                Clothing clothing = new Clothing(convertPoundToTon(airDryLbs), convertPoundToTon(secondHandLbs), convertPoundToTon(sustainableLbs), convertPoundToTon(coldWashLbs),convertPoundToTon(returnLbs) ,convertPoundToTon(totalLbs) , airDrySlider.getValue(), secondHandSlider.getValue(), sustainableSlider.getValue(),coldWashSlider.getValue(), returnSlider.getValue(),returnOnlineSlider.getValue());
                mDatabase.child(userID).child("clothing").setValue(clothing);
                updateUserFootprint(convertPoundToTon(calculateClothesTotal()));
                Toast.makeText(getContext(), "Clothing data Saved", Toast.LENGTH_SHORT).show();

            }
        });


    }
    public double convertPoundToTon(double pound) {
        return (double) (pound * 0.000453592);
        // return 1.0;
    }
//  call this method if to set the ew to be populated with the data the user already has
    public void setSliderValues(double d) {
        mDatabase.child(userID).child("clothing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clothing clothing = snapshot.getValue(Clothing.class);
                    //set all views visible
                    airDrySlider.setValue((float) clothing.getAirDryPercent());
                    secondHandSlider.setValue((float) clothing.getSecondHandPercent());
                    sustainableSlider.setValue((float) clothing.getSustainablePercent());
                    coldWashSlider.setValue((float) clothing.getColdWashPercent());
                    returnSlider.setValue((float) clothing.getReturnPercent());
                    returnOnlineSlider.setValue((float) clothing.getReturnOnlinePercent());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void updateUserFootprint(double d) {
        mDatabase.child(userID).child("footprint").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                   // mDatabase.child("footprint").child(userID).child("clothing").setValue(round(d,2));
                    mDatabase.child(userID).child("footprint").child("clothing").setValue(d);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private double calculateClothesTotal() {

        if(coldWashEdited) {//check if the user has interacted with the slider
            coldWashLbs =(43-(coldWashSlider.getValue()*.43));
        }

        if(airDryEdited) {
            airDryLbs =(447-(airDrySlider.getValue()*4.47));
        }

        if(secondHandEdited) {
            secondHandLbs =(659-(secondHandSlider.getValue()*6.59));
        }

        if(sustainableEdited) {
            sustainableLbs =(279-(sustainableSlider.getValue()*2.79));
        }

        if(returnEdited) {
            returnLbs =returnSlider.getValue()*1;//1lbs of co2 per % return from Thred up calculator
//            if(returnOnlineEdited) {
//                returnLbs=0;
//                returnLbs = ((100-returnOnlineSlider.getValue())*returnSlider.getValue())*1;//the percent of the
//                returnLbs+= (returnOnlineSlider.getValue())*returnSlider.getValue()*.4;
//            }
        }

        if(returnOnlineEdited) {
            //online returns are 60% more efficient so for every % that's returned online vs returned normally -.6lbs of carbon

            returnLbs -= ((returnSlider.getValue() * (returnOnlineSlider.getValue() / 100)) * 0.6);
        }

        return airDryLbs + secondHandLbs + sustainableLbs + coldWashLbs + returnLbs;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public class MyOnSliderTouchListener implements Slider.OnSliderTouchListener {

        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @SuppressLint("ResourceType")//to allow use of ints as ids for spinners
        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {


            switch (slider.getId()) {// this switch case takes the slider that has been used and sets its edited status to true and makes the next relative slider and question visible
                case 1:
                    //coldwash Q
                    coldWashEdited=true;
                        airDryTextView.setVisibility(View.VISIBLE);
                        airDrySlider.setVisibility(View.VISIBLE);
                        totalLayout.setVisibility(View.VISIBLE);
                    updateDisplayTotal();
                break;
                case 2:
                    //Air dry Q
                    airDryEdited=true;
                    buyingTextView.setVisibility(View.VISIBLE);
                    secondHandTextView.setVisibility(View.VISIBLE);
                    secondHandSlider.setVisibility(View.VISIBLE);
                    updateDisplayTotal();
                    break;
                case 3:
                    secondHandEdited=true;
                    sustainableTextView.setVisibility(View.VISIBLE);
                    sustainableSlider.setVisibility(View.VISIBLE);
                    updateDisplayTotal();
                    break;
                case 4:
                    //sustainable q
                    sustainableEdited=true;
                    returnTextView.setVisibility(View.VISIBLE);
                    returnSlider.setVisibility(View.VISIBLE);
                    updateDisplayTotal();
                    break;
                case 5:
                    //return q
                    returnEdited=true;
                    if(returnSlider.getValue()==0){
                        returnOnlineTextView.setVisibility(View.GONE);
                        returnOnlineSlider.setVisibility(View.GONE);
                        updateDisplayTotal();
                    }else {
                        returnOnlineTextView.setVisibility(View.VISIBLE);
                        returnOnlineSlider.setVisibility(View.VISIBLE);
                        button.setVisibility(View.VISIBLE);
                        updateDisplayTotal();
                    }
                   break;
                case 6:
                    //return online q
                    returnOnlineEdited=true;
                    updateDisplayTotal();
                    break;
            }
        }

        public void updateDisplayTotal(){
            clothesTotal.setText(String.valueOf(round(convertPoundToTon(calculateClothesTotal()),2)+"Tonnes of CO2"));
        }



    }
}
