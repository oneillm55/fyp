package com.example.fyp.ClothingFolder;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fyp.R;
import com.google.android.material.slider.Slider;

public class ClothingFragment extends Fragment {
    Slider airDrySlider, secondHandSlider, sustainableSlider, coldWashSlider;
    double airDryValue, secondHandValue, sustainableValue, coldwashValue;
    Button button;
    TextView clothesTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clothing, container, false);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        airDrySlider = view.findViewById(R.id.airDrySlider);
        secondHandSlider = view.findViewById(R.id.secondHandSlider);
        sustainableSlider = view.findViewById(R.id.sustainableSlider);
        coldWashSlider = view.findViewById(R.id.coldWashSlider);
        button = view.findViewById(R.id.clothingButton);
        clothesTotal =  view.findViewById(R.id.clothesTotal);

        setSliderValues();
        airDrySlider.setId(1);
        secondHandSlider.setId(2);
        sustainableSlider.setId(3);
        coldWashSlider.setId(4);
        MyOnSliderTouchListener listener = new MyOnSliderTouchListener();
        airDrySlider.addOnSliderTouchListener(listener);
        secondHandSlider.addOnSliderTouchListener(listener);
        sustainableSlider.addOnSliderTouchListener(listener);
        coldWashSlider.addOnSliderTouchListener(listener);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "values"+airDryValue+ secondHandValue+ sustainableValue+ coldwashValue, Toast.LENGTH_SHORT).show();
                clothesTotal.setText(String.valueOf(calculateClothesTotal()+"lbs of CO2"));

            }
        });


//        airDrySlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
//            @Override
//            public void onStartTrackingTouch(@NonNull Slider slider) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(@NonNull Slider slider) {
//                airDryValue = slider.getValue();
//            }
//        });

    }

    private void setSliderValues() {
        airDryValue = airDrySlider.getValue();
        secondHandValue = secondHandSlider.getValue();
        sustainableValue = sustainableSlider.getValue();
        coldwashValue = coldWashSlider.getValue();
    }

    private double calculateClothesTotal() {
       // double d=(secondHandValue*6.59)+(airDryValue*4.47)+(sustainableValue*2.79)+(coldwashValue*.43);
        return ((100-secondHandValue)*6.59)+(447-(airDryValue*4.47))+(279-(sustainableValue*2.79))+((coldwashValue*8.6));//use 8.6 for coldwash because its 43/5(the number of washes a week)
    }

    public class MyOnSliderTouchListener implements Slider.OnSliderTouchListener {

        @Override
        public void onStartTrackingTouch(@NonNull Slider slider) {

        }

        @Override
        public void onStopTrackingTouch(@NonNull Slider slider) {

            switch (slider.getId()) {
                case 1:
                    airDryValue = slider.getValue();
                    clothesTotal.setText(String.valueOf(calculateClothesTotal()+"lbs of CO2"));
                    break;
                case 2:
                    secondHandValue = slider.getValue();
                    clothesTotal.setText(String.valueOf(calculateClothesTotal()+"lbs of CO2"));
                    break;
                case 3:
                    sustainableValue = slider.getValue();
                    clothesTotal.setText(String.valueOf(calculateClothesTotal()+"lbs of CO2"));
                    break;
                case 4:
                    coldwashValue = slider.getValue();
                    clothesTotal.setText(String.valueOf(calculateClothesTotal()+"lbs of CO2"));
                    break;
            }
        }


    }
}
