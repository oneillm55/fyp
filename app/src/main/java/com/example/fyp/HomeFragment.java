package com.example.fyp;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;




public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private PieChart pieChart;
    private BarChart barChart;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private double foodCO2,flightCO2,clothingCO2,totalCO2;
    private TextView totalCO2TextView;
    private LinearLayout pieChartLegendLayout;
    private boolean userHasData;
    private ArrayList<Integer> colors;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // colors array has to be done in on create as it uses gerResources()
        colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.blue_jeans));
        colors.add(getResources().getColor(R.color.cyber_yellow));
        colors.add(getResources().getColor(R.color.heat_wave));
        firebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("footprint").child(firebaseAuth.getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    //load the page for a user that has carried out the data input
                    userHasData=true;
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    flightCO2=footprint.getFlight();
                    clothingCO2=footprint.getClothing();
                    foodCO2=footprint.getFood();
                    totalCO2=foodCO2+flightCO2+clothingCO2;
                    setUpPieChart();
                    loadPieChartData();
                    setUpBarChart();
                    loadBarChartData();
                    // biggest emitter
                    //barchart for each of 3
                    totalCO2TextView.setText("Annual CO2 output:\n"+String.format("%.2f", totalCO2)+" Tonnes");
                }else {
                    //load basic home page
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
        //load generic home page
        pieChart = view.findViewById(R.id.pieChart);
        pieChartLegendLayout = view.findViewById(R.id.pieChartLegend);
        totalCO2TextView = view.findViewById(R.id.totalCO2);
        barChart = view.findViewById(R.id.barChart);

//        setUpPieChart();
//        loadPieChartData();
//        setUpBarChart();
//        loadBarChartData();
    }

    private void setUpBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.setDrawBorders(false);
        barChart.setClickable(false);
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
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

//        if(userHasData){//load bar chart data relevant to user
        if(true){
            labels.add("You");
            entries.add(new BarEntry(0, (float) totalCO2));
            colors.add(Color.rgb(26, 201, 53));
        }

        labels.add("Ireland");
        labels.add("EU");
        labels.add("UK");
        labels.add("US");
        labels.add("Worldwide");


        entries.add(new BarEntry(1, 13.2F));
        entries.add(new BarEntry(2, 8.8F));
        entries.add(new BarEntry(3, 8.3F));
        entries.add(new BarEntry(4, 15.5F));
        entries.add(new BarEntry(5, 4.5F));

        colors.add(Color.rgb(19, 135, 37));
        colors.add(Color.rgb(19, 135, 37));
        colors.add(Color.rgb(19, 135, 37));
        colors.add(Color.rgb(19, 135, 37));
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
        pieChartLegendLayout.setVisibility(View.VISIBLE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("CO2");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setTouchEnabled(true);
        pieChart.setOnChartValueSelectedListener(this);

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


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("I clicked on", String.valueOf(h.getX()) );
        //Toast.makeText(getContext(), "chart clicked"+e.getData().toString(), Toast.LENGTH_SHORT).show();
        switch(String.valueOf(h.getX())){
            case "0.0":
                //flight clicked
                Log.i("I clicked on flight", String.valueOf(h.getX()) );
                break;
            case "1.0":
                //food clicked
                Log.i("I clicked on food", String.valueOf(h.getX()) );
                break;
            case "2.0":
                //clothing clicked
                Log.i("I clicked on clothing", String.valueOf(h.getX()) );
                break;

        }


    }

    @Override
    public void onNothingSelected() {
        Log.i("nothing selected","a" );
        //set information invisible

    }
}