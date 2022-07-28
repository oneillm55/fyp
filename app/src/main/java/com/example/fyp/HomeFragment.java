package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fyp.UserFolder.User;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private PieChart pieChart;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    double foodCO2,flightCO2,clothingCO2,totalCO2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("footprint").child(firebaseAuth.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    flightCO2=footprint.getFlight();
                    //Toast.makeText(getActivity(),"flStr"+flightCO2, Toast.LENGTH_SHORT).show();
                    clothingCO2=footprint.getClothing();
                    foodCO2=footprint.getFood();
                    //totalCO2=foodCO2+flightCO2+clothingCO2;
                    setUpPieChart();
                    loadPieChartData();
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
        // Toast.makeText(getActivity(),"flStr2"+flightCO2, Toast.LENGTH_LONG).show();
        totalCO2=foodCO2+flightCO2+clothingCO2;
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        setUpPieChart();
        loadPieChartData();
        //Toast.makeText(getActivity(),"id "+firebaseAuth.getUid()+"fl "+flightCO2+" c "+clothingCO2+" fo "+foodCO2, Toast.LENGTH_SHORT).show();
    }
    public void setUpPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("CO2");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(true);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);


    }

    public void loadPieChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)(flightCO2/1),"Flights"));
        entries.add(new PieEntry((float)(foodCO2/1),"Food"));
        entries.add(new PieEntry((float)(clothingCO2/1),"Clothing"));
//        entries.add(new PieEntry((float)(flightCO2/totalCO2),"Flights"));
//        entries.add(new PieEntry((float)(foodCO2/totalCO2),"Food"));
//        entries.add(new PieEntry((float)(clothingCO2/totalCO2),"Clothing"));

          ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(26, 201, 53));
        colors.add(Color.rgb(19, 135, 37));
        colors.add(Color.rgb(11, 179, 25));


        PieDataSet dataSet = new PieDataSet(entries, "Carbon footprint");
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        pieChart.setData(data);
        pieChart.invalidate();
    }


}