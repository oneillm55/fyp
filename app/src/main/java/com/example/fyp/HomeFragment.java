package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.UserFolder.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
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
    private BarChart barChart;
    FirebaseAuth firebaseAuth;
    DatabaseReference mDatabase;
    double foodCO2,flightCO2,clothingCO2,totalCO2=13.3;
    TextView totalCO2TextView;

    public HomeFragment() {
        // Required empty public constructor
    }


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
                    totalCO2=foodCO2+flightCO2+clothingCO2;
                    setUpPieChart();
                    loadPieChartData();

                    totalCO2TextView.setText(totalCO2+"Total CO2");
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChart = view.findViewById(R.id.pieChart);
        setUpPieChart();
        loadPieChartData();
        barChart = view.findViewById(R.id.barChart);
        setUpBarChart();
        loadBarChartData();

        totalCO2TextView = view.findViewById(R.id.totalCO2);
    }

    private void setUpBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.setDrawBorders(false);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularityEnabled(true);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }
    private void loadBarChartData() {
        ArrayList<String> labels = new ArrayList<>();
        labels.add("You");
        labels.add("EU Average");

        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) totalCO2));
        entries.add(new BarEntry(1, 8.8F));

        ArrayList<Integer> colors = new ArrayList<>();

        colors.add(Color.rgb(26, 201, 53));
        colors.add(Color.rgb(19, 135, 37));


        BarDataSet dataSet = new BarDataSet(entries,"You vs Average");
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.invalidate();
    }


    public void setUpPieChart(){
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("CO2");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    public void loadPieChartData(){
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float)(flightCO2),"Flights"));
        entries.add(new PieEntry((float)(foodCO2),"Food"));
        entries.add(new PieEntry((float)(clothingCO2),"Clothing"));
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