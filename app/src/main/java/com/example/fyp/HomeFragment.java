package com.example.fyp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.ClothingFolder.Clothing;
import com.example.fyp.FlightFolder.Flight;
import com.example.fyp.FoodFolder.Food;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private PieChart pieChart;
    private BarChart barChart;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase,footprintReference;
    private  double foodCO2,flightCO2,clothingCO2,totalCO2;
    private TextView totalCO2TextView, recTitle,recParagraph, barChartTitle;
    private LinearLayout pieChartLayout, barChartLayout,recLayout,big3Layout;
    private boolean userHasData;
    private ArrayList<Integer> colors;
    Footprint userFootprint = new Footprint();
//    Clothing userClothing;
//    Food userFood;
//    private List<Flight> userFlightList;
    private String userID ,recParagraphContent;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        userID=firebaseAuth.getCurrentUser().getUid();
        mDatabase= FirebaseDatabase.getInstance().getReference();

         mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    //userFootprint=footprint;
                   // ((MyApplication) getActivity().getApplication()).setUserFootprint(footprint);

                        flightCO2=footprint.getFlight();
                        clothingCO2=footprint.getClothing();
                        foodCO2=footprint.getFood();
                        totalCO2=foodCO2+flightCO2+clothingCO2;
                        totalCO2TextView.setText("Annual CO2 output:\n"+String.format("%.2f", totalCO2)+" Tonnes");
                        userFootprint = footprint;

                    setUpPieChart();
                    loadPieChartData();
                }

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        mDatabase.child(userID).child("clothing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clothing clothing = snapshot.getValue(Clothing.class);
                    //I need to be able to compare the clothing object to the userFootprint but userfootprint can on be accessed insode the onDataChange for the footprint query
                  //  userFootprint.getClothing();
                    WashingRec washingRec= washing(clothing);
                    PurchasingRec purchasingRec = purchasing(clothing);
                    ReturnClothingRec returnClothingRec = returnClothing(clothing);

                    setClothingRec(washingRec,purchasingRec,returnClothingRec, clothing);

                }else{

                }
            }


            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        // colors array has to be done in on create as it uses getResources()
        colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.blue_jeans));
        colors.add(getResources().getColor(R.color.cyber_yellow));
        colors.add(getResources().getColor(R.color.heat_wave));
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    private void setClothingRec(WashingRec washingRec, PurchasingRec purchasingRec, ReturnClothingRec returnClothingRec, Clothing clothing) {

        double washing = washingRec.getTotalWashingTSaved(), purchasing  = purchasingRec.getTotalPurchasingTSaved(), returnClothing = returnClothingRec.getTotalReturnTSaved();
        if(biggest(washing,purchasing,returnClothing)){

            recParagraphContent = "We recommend changing your washing habits\n Save "+ String.valueOf(round(washing,2));
            if(washingRec.isAirDry()){
                recParagraphContent +="\n By air drying your laundry you could save "+ String.valueOf(round(washingRec.getAirDryTSaved(),2)+" tonnes of CO2");
            }

            if(washingRec.isColdWash()){
                recParagraphContent +="\n By doing you laundry on a cold wash you could save "+ String.valueOf(round(washingRec.getColdWashTSaved(),2)+" tonnes of CO2");
            }
        }else if(biggest(purchasing,washing,returnClothing)){

            recParagraphContent = "We recommend changing your purchasing habits\n Save "+ String.valueOf(round(purchasing,2));
        }else if(biggest(returnClothing,washing,purchasing)){

            recParagraphContent = "We recommend changing your shopping habits\n Save "+ String.valueOf(round(returnClothing,2));
        }else{
                //two of the values are equal
            recParagraphContent = "You could benefit from changing more than one habit regarding your clothing consumption ";
    }

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load generic home page

        totalCO2TextView = view.findViewById(R.id.totalCO2);
        pieChart = view.findViewById(R.id.pieChart);
        pieChartLayout = view.findViewById(R.id.pieChartLayout);
        barChart = view.findViewById(R.id.barChart);
        barChartTitle= view.findViewById(R.id.barChartTitle);
        barChartLayout = view.findViewById(R.id.barChartLayout);
        recLayout = view.findViewById(R.id.recommendationLayout);
        recTitle= view.findViewById(R.id.recommendationTitle);
        recParagraph=view.findViewById(R.id.recommendationParagraph);
        big3Layout = view.findViewById(R.id.big3Layout);
    //    Footprint userFootprint = ((MyApplication) getActivity().getApplication()).getUserFootprint();
    //   Log.i("!!!!!", String.valueOf(userFootprint.getFlight()));
     //get user data
    }

    private WashingRec washing(Clothing clothing) {

        double airDryT=clothing.getAirDryT(), coldwashT=clothing.getColdWashT(), savedT=0,coldwashPercent = clothing.getColdWashPercent(),airDryPercent = clothing.getAirDryPercent();
        WashingRec wr = new WashingRec();
        //Reccomendation is to cold wash 100% of clothes and air dry 100%
        // if((coldwashPercent*1.50)<100){//if user isnt currently at the recomended % of cold washesif its possible to wash 50% more on cold
        if(coldwashPercent<100){//if user isnt currently at the recomended % of cold washes
            //reccomend cold washing more
            wr.setColdWash(true);
            wr.setColdWashTSaved(coldwashT);
        }

        if(airDryPercent<100){
            //reccomend airdry more
            wr.setAirDry(true);
            wr.setAirDryTSaved(airDryT);
        }

        return wr;


    }

    private PurchasingRec purchasing(Clothing clothing) {
        PurchasingRec pr = new PurchasingRec();
        double secondHandT=clothing.getSecondHandT(),sustainableT=clothing.getSustainableT(),secondHandPercent=clothing.getSecondHandPercent(),sustainablePercent=clothing.getSustainablePercent() ;

        //Reccomend purchase 50% sustainable and 50% more second hand

        if(sustainablePercent<50){
            //reccomend sus more
            pr.setSustainable(true);
            //the amount the user would save if they purchased 50% of their clothes sustainably
            //sustaibableT = current cost of purchasing sustaiablePercent of clothes sustaiably

            double costFifity = 0.063276084;//cost of a user purchasing 50% of their clothes sustaiably
           double sustainableTSaved = sustainableT-costFifity; //difference between users current cost and the cost of them purchasing 50% sustainably ie their potential savings
           pr.setSustainableTSaved(sustainableTSaved);
        }
        if(secondHandPercent<(sustainablePercent*1.50)){//user can purchase 50% more second hand
            //reccomend more second hand
            double secondHandTSaved = secondHandT*.5;//amount they could save if they purchase 50% more second hand
            pr.setSecondHand(true);
            pr.setSecondHandTSaved(secondHandTSaved);
        }
        return pr;
    }

    private ReturnClothingRec returnClothing(Clothing clothing){
        ReturnClothingRec rcr = new ReturnClothingRec();
        double returnT = clothing.getReturnT(),returnOnlineT = clothing.getReturnT(),returnPercent = clothing.getReturnPercent(), returnOnlinePercent = clothing.getReturnOnlinePercent();

        //recommend not returning any orders if you are return them online
        if (returnPercent>0){
            rcr.setReturnClothing(true);
            rcr.setReturnTSaved(returnT);

            if(returnOnlinePercent!=100){//if user is not already doing 100% of returns online
                rcr.setReturnOnline(true);
                //online orders are 60% more efficient(Thred up) therefore returning all orders online would reduce by 60%
                rcr.setReturnOnlineTSaved(returnT*.4);//the amount you would save by doing 100% of returns online
            }
        }
        return rcr;
    }

    public void setUserFootprint(){
        mDatabase.child("footprint").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    //userFootprint =footprint;
                }

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){
                Toast.makeText(getContext(), "Db error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void readData(FirebaseCallback firebaseCallback){

        mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    firebaseCallback.onCallBack(footprint);
                }

            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){
                //Toast.makeText(getContext(), "Db error", Toast.LENGTH_SHORT).show();

            }
        });

        mDatabase.child(userID).child("clothing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clothing clothing = snapshot.getValue(Clothing.class);
                    //do whats required with user clothing
                    Log.i("in clothing", String.valueOf(clothing.getAirDryT()));

                    Toast.makeText(getContext(), "in clothing"+String.valueOf(clothing.getAirDryT()), Toast.LENGTH_SHORT).show();
                    firebaseCallback.onCallBack(clothing);
                }else{
                    Log.i("in clothing","Doesnt exist");
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        mDatabase.child(userID).child("food").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Food food = snapshot.getValue(Food.class);
                    //do whats required with user food
                    firebaseCallback.onCallBack(food);
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });

        mDatabase.child(userID).child("flights").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    List<Flight> flightList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Flight flight = dataSnapshot.getValue(Flight.class);
                        flightList.add(flight);
                        //do whats required with user flights
                    }
                    firebaseCallback.onCallBack(flightList);
                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });


    };

    private interface FirebaseCallback{
       void onCallBack(Footprint footprint);
        void onCallBack(Clothing clothing);
        void onCallBack(List<Flight> flightList);
        void onCallBack(Food food);
    }

    private void setBig3(double d) {
        //go to the database, find the 3 biggest culprits for users footprint, set the big three to be those 3 in order of tonnes of co2 saved
        Toast.makeText(getContext(), "d"+String.valueOf(d), Toast.LENGTH_SHORT).show();

    }
    private void handleFood() {
        FirebaseDatabase.getInstance().getReference("food").child(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Food food = snapshot.getValue(Food.class);
                    //do whats required with user food

                }
            }
            @Override
            public void onCancelled (@NonNull DatabaseError error){

            }
        });
    }




    private void setUpBarChart() {
        barChartLayout.setVisibility(View.VISIBLE);
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
        if(userHasData){
            labels.add("You");
            entries.add(new BarEntry(0, (float) totalCO2));
            colors.add(Color.rgb(26, 201, 53));
            barChartTitle.setText("You vs Rest of World");
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
        pieChartLayout.setVisibility(View.VISIBLE);
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
        //handles the section of the pie chart clicked
        switch(String.valueOf(h.getX())){
            case "0.0":
                //flight clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Flights");
                recParagraph.setText("fly less lad");
                Log.i("I clicked on flight", String.valueOf(h.getX()) );
                break;
            case "1.0":
                //food clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Food");
                recParagraph.setText("Eat less lad");
                Log.i("I clicked on food", String.valueOf(h.getX()) );
                break;
            case "2.0":
                //clothing clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Clothing");
                recParagraph.setText(recParagraphContent);
                Log.i("I clicked on clothing", String.valueOf(h.getX()) );
                break;

        }


    }
    @Override
    public void onNothingSelected() {
        recLayout.setVisibility(View.GONE);
        Log.i("nothing selected","a" );
        //set information invisible

    }

    public double convertPoundToTon(double pound) {
        return (double) (pound * 0.000453592);
        // return 1.0;
    }

    public double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean biggest(double max,double val1, double val2) {
        return max > val1 && max > val2;
    }

}