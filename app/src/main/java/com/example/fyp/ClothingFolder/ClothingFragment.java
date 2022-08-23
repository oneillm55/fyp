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
                Clothing clothing = new Clothing(airDryLbs, secondHandLbs, sustainableLbs, coldWashLbs, returnLbs,totalLbs, airDrySlider.getValue(), secondHandSlider.getValue(), sustainableSlider.getValue(),coldWashSlider.getValue(), returnSlider.getValue(),returnOnlineSlider.getValue());
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

        if(coldWashEdited) {
            coldWashLbs =(43-(coldWashSlider.getValue()*.43));//todo fix calculations
        }

        if(airDryEdited) {//check if the user has interacted with the slider
            airDryLbs =(447-(airDrySlider.getValue()*4.47));//todo fix calculations
        }

        if(secondHandEdited) {
            secondHandLbs =(659-(secondHandSlider.getValue()*6.59));
        }

        if(sustainableEdited) {
            sustainableLbs =(279-(sustainableSlider.getValue()*2.79));
        }

        if(returnEdited) {
            returnLbs =returnSlider.getValue()*1;//1lbs od co2 per return from Thred up calculator
        }

        if(returnOnlineEdited) {
            returnLbs = returnLbs -(returnOnlineSlider.getValue()*0.6);//online returns are 60% more efficient so for every % that's returned online vs returned normally -.6lbs of carbon
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
