package com.example.fyp;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fyp.ClothingFolder.Clothing;
import com.example.fyp.FlightFolder.Flight;
import com.example.fyp.FoodFolder.Food;
import com.example.fyp.RecommendationsFolder.ClothingRecs.PurchasingRec;
import com.example.fyp.RecommendationsFolder.ClothingRecs.ReturnClothingRec;
import com.example.fyp.RecommendationsFolder.ClothingRecs.WashingRec;
import com.example.fyp.RecommendationsFolder.FlightRecs.FlightClassRec;
import com.example.fyp.RecommendationsFolder.FoodRecs.CompostRec;
import com.example.fyp.RecommendationsFolder.FoodRecs.DairyRec;
import com.example.fyp.RecommendationsFolder.FoodRecs.MeatRec;
import com.example.fyp.RecommendationsFolder.FoodRecs.OrganicRec;
import com.example.fyp.RecommendationsFolder.FoodRecs.ShoppingRec;
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
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private PieChart pieChart, foodPieChart;
    private BarChart barChart;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase, footprintReference;
    private double foodCO2, flightCO2, clothingCO2, totalCO2, newFlightFootprint,meatCO2, shoppingCO2, dairyCO2, compostCO2,organicCO2 ;
    private TextView totalCO2TextView, recTitle, recParagraph, barChartTitle;
    private LinearLayout pieChartLayout,foodPieChartLayout, barChartLayout, recLayout, big3Layout;
    private boolean userHasData;
    private ArrayList<Integer> colors,foodColors;
    private String userID, clothingRecContent, foodRecContent, flightRecContent, goClimateApiKey;
    private String level="Conservative";
    Footprint userFootprint = new Footprint();
    CountDownLatch latch = new CountDownLatch(1);

//    Clothing userClothing;
//    Food userFood;
//    private List<Flight> userFlightList;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        goClimateApiKey=getString(R.string.go_climate_api_key);


        readData(new FirebaseCallback() {
            @Override
            public void onCallBack(Footprint footprint) {
                loadFootprintViews(footprint);
            }

            @Override
            public void onCallBack(Clothing clothing) {
                // a way for the app to use both the clothing and the footprint at once
                mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                            Footprint footprint = snapshot.getValue(Footprint.class);

                            loadClothingViews(clothing,footprint);


                            //if footprint.get largest element is clothing set clothing rec to be at top of screen else set them to be in the rec paragraph for clothing
                        }
                    }
                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });
            }

            @Override
            public void onCallBack(List<Flight> flightList) {
                loadFlightViews(flightList);

            }

            @Override
            public void onCallBack(Food food) {
                mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                            Footprint footprint = snapshot.getValue(Footprint.class);

                            loadFoodViews(food,footprint);
                        }
                    }
                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });

            }
        });

        // colors array has to be done in on create as it uses getResources()
        colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.blue_jeans));
        colors.add(getResources().getColor(R.color.cyber_yellow));
        colors.add(getResources().getColor(R.color.heat_wave));
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    private void loadFlightViews(List<Flight> flightList) {
        FlightClassRec flightClassRec = getFlightClassRec(flightList);

        setFlightRec(flightList,flightClassRec);
    }

    private void setFlightRec(List<Flight> flightList, FlightClassRec flightClassRec) {
        flightRecContent="By flying economy on all your flights you could save "+String.valueOf(round(flightClassRec.getTSaved(),2));
    }

    private FlightClassRec getFlightClassRec(List<Flight> flightList) {
        FlightClassRec fcr = new FlightClassRec();
        //take the flight class loop it for every flight check class if not economy, calculate cost of flight if economy and add it to a new list then get the total flight cost for the new list vs old
        double newTotal=0;//stores the new flight total if the user took all their flights economy
        double currentTotal=0;// the users current flight footprint
       for(Flight flight : flightList){
           currentTotal+=flight.getFootprint();//get the current total footprint of all flights
           if(flight.getFlightClass().trim().equalsIgnoreCase("Economy")){
               //add flight co2 to the new list
               newTotal+=flight.getFootprint();
           }else{
               //flight is business or first class therefore get its potention co2 if it were economy
               getNewFlightFootprint(flight.getDepart(), flight.getArrive(), flight.isReturnFlight());//this will set newFlight footprint to be the value of the current flight if flown economy
               //use new flight footprint value
               try {
                   latch.await();//wait for newFlightFootprint to be assigned
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               newTotal+=newFlightFootprint;
                 Log.i("!!!!!newFF", String.valueOf(newFlightFootprint));
               newFlightFootprint=0;
           };

       }
       //total of all current flights - the total of flights if they were all economy
        double totalSaved = currentTotal-newTotal;

       fcr.setTSaved(totalSaved);
       return fcr;

    }

    private void loadFoodViews(Food food, Footprint footprint) {
        meatCO2=food.getMeatValue();
        dairyCO2=food.getDairyValue();
        shoppingCO2=food.getShoppingValue();
        compostCO2=food.getCompostValue();
        organicCO2=food.getOrganicValue();
        setUpFoodPieChart();
        loadFoodPieChartData();
        MeatRec meatRec = getMeatRec(food, footprint);
        DairyRec dairyRec = getDairyRec(food,footprint);
        ShoppingRec shoppingRec = getShoppingRec(food,footprint);
        CompostRec compostRec = getCompostRec(food);
        OrganicRec organicRec = getOrganicRec(food,footprint);

        setFoodRec(food, meatRec, dairyRec, shoppingRec, compostRec, organicRec);
    }

    private void loadFoodPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) (meatCO2), "Meat"));
        entries.add(new PieEntry((float) (dairyCO2), "Dairy"));
        entries.add(new PieEntry((float) (shoppingCO2), "Shopping"));
        entries.add(new PieEntry((float) (compostCO2), "Compost"));
        entries.add(new PieEntry((float) (organicCO2), "Organic"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        foodColors = new ArrayList<>();
        foodColors.add(Color.parseColor("#FF0000"));
        foodColors.add(Color.parseColor("#FF7300"));
        foodColors.add(Color.parseColor("#52D726"));
        foodColors.add(Color.parseColor("#FFEC00"));
        foodColors.add(Color.parseColor("#007ED6"));
        dataSet.setColors(foodColors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(foodPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        foodPieChart.setData(data);
        foodPieChart.invalidate();
    }

    private void setUpFoodPieChart() {
       // foodPieChartLayout.setVisibility(View.VISIBLE);
        foodPieChart.setDrawHoleEnabled(true);
        foodPieChart.setUsePercentValues(true);
        foodPieChart.setDrawEntryLabels(true);
        foodPieChart.setEntryLabelTextSize(12);
        foodPieChart.setEntryLabelColor(Color.BLACK);
        foodPieChart.setCenterText("Food");
        foodPieChart.setCenterTextSize(24);
        foodPieChart.getDescription().setEnabled(false);
        foodPieChart.setDrawEntryLabels(false);
        foodPieChart.setTouchEnabled(false);

        Legend l = foodPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(10);
        l.setEnabled(true);
    }

    private void setFoodRec(Food food, MeatRec meatRec, DairyRec dairyRec, ShoppingRec shoppingRec, CompostRec compostRec, OrganicRec organicRec) {
        //set the values on the homepage with the recomendations that correspond to the passed parameters
        double meat = meatRec.getTSaved(), dairy = dairyRec.getTSaved(), shopping = shoppingRec.getTSaved(), compost = compostRec.getTSaved(), organic = organicRec.getTSaved();
        if (biggestOfFive(meat, dairy, shopping, compost, organic)) {
            foodRecContent="Biggest saving= meat\n Save"+ round(meat,2)+meatRec.getSuggestion();
        } else if (biggestOfFive(dairy, shopping, compost, organic, meat)) {
            foodRecContent="Biggest saving= dairy\n Save"+ round(dairy,2) + dairyRec.getSuggestion();
        } else if (biggestOfFive(shopping, compost, organic, meat, dairy)) {
            foodRecContent="Biggest saving= shopping\n Save"+ round(shopping,2)+ shoppingRec.getSuggestion();
        } else if (biggestOfFive(compost, organic, meat, dairy, shopping)) {
            foodRecContent="Biggest saving= compost\n Save"+ round(compost,2)+ compostRec.getSuggestion();
        } else if(biggestOfFive(organic, meat, dairy, shopping,compost)) {
            foodRecContent="Biggest saving= organic\n Save"+ round(organic,2)+ organicRec.getSuggestion();
        }else{
                //two values are equal
            //get the two equal values and display the recomendation for the one with the larger overall output
        }
    }

    private OrganicRec getOrganicRec(Food food, Footprint footprint) {
        OrganicRec or = new OrganicRec();
        String organicString = food.getOrganicString(), suggestion = "";
        double organicValue = food.getOrganicValue(), organicTSaved = 0;
        switch (organicString) {

            case "Most":
                //user already buying most food organically cant save carbon
                break;
            case "Some":
                organicTSaved = organicValue - 0.15;
                suggestion = "Most";
                break;
            case "None":
                // value = value + .06;
                if ((ambitious(footprint))) {
                    //most
                    organicTSaved = organicValue - 0.15;
                    suggestion = "Most";
                } else {
                    //some
                    organicTSaved = organicValue - .03;
                    suggestion = "Some";
                }
                break;
        }
        or.setTSaved(organicTSaved);
        or.setSuggestion(organicString);
        return or;

    }

    private CompostRec getCompostRec(Food food) {
        CompostRec cr = new CompostRec();
        String compostString = food.getCompostString(), suggestion = "";
        double compostValue = food.getCompostValue(), compostTSaved = 0;
        switch (compostString) {
            case "Yes":
                break;
            case "No":
                compostTSaved = compostValue;
                suggestion = "Yes";
                break;
        }
        cr.setTSaved(compostTSaved);
        cr.setSuggestion(suggestion);
        return cr;
    }

    private ShoppingRec getShoppingRec(Food food, Footprint footprint) {
        ShoppingRec sr = new ShoppingRec();
        String shoppingString = food.getShoppingString(), suggestion = "";
        double shoppingValue = food.getShoppingValue(), shoppingTSaved = 0;
        switch (shoppingString) {
            case "":
                break;
            case "Only local produce":
                //user already doing best they can
                break;
            case "Mostly Local, some supermarkets":
                shoppingTSaved = shoppingValue - 0.03;
                suggestion = "Only local produce";

                break;
            case "Mostly supermarkets, try to buy Irish":
                if ((ambitious(footprint))) {
                    shoppingTSaved = shoppingValue - 0.03;
                    suggestion = "Only local produce";
                } else {
                    shoppingTSaved = shoppingValue - 0.7;
                    suggestion = "Mostly Local, some supermarkets";
                }
                break;
            case "All supermarkets, pay no attention to country of origin":
                if ((ambitious(footprint))) {
                    shoppingTSaved = shoppingValue - 0.03;
                    suggestion = "Only local produce";
                } else {
                    shoppingTSaved = shoppingValue - 0.15;
                    suggestion = "Mostly supermarkets, try to buy Irish";
                }
                break;
        }
        sr.setSuggestion(suggestion);
        sr.setTSaved(shoppingTSaved);
        return sr;
    }

    private DairyRec getDairyRec(Food food,Footprint footprint) {
        DairyRec dr = new DairyRec();
        String dairyString = food.getDairyString(), suggestion = "";
        double dairyValue = food.getDairyValue(), dairyTSaved = 0;
        switch (dairyString) {
            case "Never":
                break;
            case "Occasionally":
                //value = value + .04;
                break;
            case "Every day":
                //value = value + .07;
                if ((ambitious(footprint))) {
                    dairyTSaved = dairyValue;
                    suggestion = "Never";

                } else {
                    dairyTSaved = dairyValue - .04;
                    suggestion = "Occasionally";
                }
                break;
        }

        dr.setSuggestion(suggestion);
        dr.setTSaved(dairyTSaved);
        return dr;
    }

    private MeatRec getMeatRec(Food food,Footprint footprint) {
        MeatRec mr = new MeatRec();
        String meatString = food.getMeatString(), suggestion = "";
        double meatValue = food.getMeatValue(), meatTSaved = 0;
        switch (meatString) {
            case "Never":
                break;
            case "Occasionally":
                //user already eats meat rarely, maybe consider cutting it out all together
                meatTSaved = meatValue;
                suggestion = "Never";

                break;
            case "Once a day":
                //user is eating meat once a day if they are serious they might consider removing meat from diet else recomend occasionally
                if ((ambitious(footprint))) {
                    meatTSaved = meatValue;
                    suggestion = "Never";
                } else {
                    meatTSaved = meatValue - .08;
                    suggestion = "Occasionally";

                }
                break;
            case "Most meals":
                //if you ate once a day instead
                if ((ambitious(footprint))) {

                    meatTSaved = meatValue - .08;
                    suggestion = "Occasionally";
                } else {
                    meatTSaved = meatValue - .21;
                    suggestion = "Once a day";
                }
                break;
        }
        mr.setTSaved(meatTSaved);
        mr.setSuggestion(suggestion);
        return mr;
    }

    private void loadClothingViews(Clothing clothing,Footprint footprint) {
        WashingRec washingRec = getWashingRec(clothing,footprint);
        PurchasingRec purchasingRec = getPurchasingRec(clothing);
        ReturnClothingRec returnClothingRec = getReturnClothingRec(clothing);

        setClothingRec(washingRec, purchasingRec, returnClothingRec, clothing,footprint);
    }

    private void setClothingRec(WashingRec washingRec, PurchasingRec purchasingRec, ReturnClothingRec returnClothingRec, Clothing clothing, Footprint footprint) {

        double washing = washingRec.getTotalWashingTSaved(), purchasing = purchasingRec.getTotalPurchasingTSaved(), returnClothing = returnClothingRec.getTotalReturnTSaved();
        if (biggestOfThree(washing, purchasing, returnClothing)) {

            clothingRecContent = "We recommend changing your washing habits\n Save " + String.valueOf(round(washing, 2));
            if (washingRec.isAirDry()) {
                clothingRecContent += "\n By air drying your laundry you could save " + String.valueOf(round(washingRec.getAirDryTSaved(), 2) + " tonnes of CO2");
            }

            if (washingRec.isColdWash()) {
                clothingRecContent += "\n By doing you laundry on a cold wash you could save " + String.valueOf(round(washingRec.getColdWashTSaved(), 2) + " tonnes of CO2");
            }
        } else if (biggestOfThree(purchasing, washing, returnClothing)) {

            clothingRecContent = "We recommend changing your purchasing habits\n Save " + String.valueOf(round(purchasing, 2));
        } else if (biggestOfThree(returnClothing, washing, purchasing)) {

            clothingRecContent = "We recommend changing your shopping habits\n Save " + String.valueOf(round(returnClothing, 2));
        } else {
            //two of the values are equal
            clothingRecContent = "You could benefit from changing more than one habit regarding your clothing consumption ";
        }

    }

    private WashingRec getWashingRec(Clothing clothing,Footprint footprint) {

        double airDryT = clothing.getAirDryT(), coldwashT = clothing.getColdWashT(), savedT = 0, coldwashPercent = clothing.getColdWashPercent(), airDryPercent = clothing.getAirDryPercent();
        WashingRec wr = new WashingRec();
        //High ambitious recommendation is to cold wash 100% of clothes and air dry 100%
        //Moderate ambitious recommendation is to cold wash 80% of clothes and air dry 80%
        // if((coldwashPercent*1.50)<100){//if user isnt currently at the recomended % of cold washesif its possible to wash 50% more on cold
        if (coldwashPercent < 100) {//if user isnt currently at the recomended % of cold washes
            //recommend cold washing more
            if(footprint.getAmbition().equalsIgnoreCase("High")){
                wr.setColdWashSuggestion("We recommend you do all your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT)+"t of CO2 annually");
            }else if (coldwashPercent < 80){

                wr.setColdWashSuggestion("We recommend you do 80% your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT*.8)+"t of CO2 annually");
            }else{
                //user has moderate ambition and is already cloding washing between 80-99% of their clothes
            }
            wr.setColdWash(true);
            wr.setColdWashTSaved(coldwashT);
        }

        if (airDryPercent < 100) {
            //reccomend airdry more
            if(footprint.getAmbition().equalsIgnoreCase("High")){
                wr.setColdWashSuggestion("We recommend you do all your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT)+"t of CO2 annually");
            }else if (airDryPercent < 80){

                wr.setColdWashSuggestion("We recommend you do 80% your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT*.8)+"t of CO2 annually");
            }else{
                //user has moderate ambition and is already airdrying between 80-99% of their clothes
            }
            wr.setAirDry(true);
            wr.setAirDryTSaved(airDryT);
        }

        return wr;


    }

    private PurchasingRec getPurchasingRec(Clothing clothing) {
        PurchasingRec pr = new PurchasingRec();
        double secondHandT = clothing.getSecondHandT(), sustainableT = clothing.getSustainableT(), secondHandPercent = clothing.getSecondHandPercent(), sustainablePercent = clothing.getSustainablePercent();

        //Reccomend purchase 50% sustainable and 50% more second hand

        if (sustainablePercent < 50) {
            //reccomend sus more
            pr.setSustainable(true);
            //the amount the user would save if they purchased 50% of their clothes sustainably
            //sustaibableT = current cost of purchasing sustaiablePercent of clothes sustaiably

            double costFifity = 0.063276084;//cost of a user purchasing 50% of their clothes sustaiably
            double sustainableTSaved = sustainableT - costFifity; //difference between users current cost and the cost of them purchasing 50% sustainably ie their potential savings
            pr.setSustainableTSaved(sustainableTSaved);
        }
        if (secondHandPercent < (sustainablePercent * 1.50)) {//user can purchase 50% more second hand
            //reccomend more second hand
            double secondHandTSaved = secondHandT * .5;//amount they could save if they purchase 50% more second hand
            pr.setSecondHand(true);
            pr.setSecondHandTSaved(secondHandTSaved);
        }
        return pr;
    }

    private ReturnClothingRec getReturnClothingRec(Clothing clothing) {
        ReturnClothingRec rcr = new ReturnClothingRec();
        double returnT = clothing.getReturnT(), returnOnlineT = clothing.getReturnT(), returnPercent = clothing.getReturnPercent(), returnOnlinePercent = clothing.getReturnOnlinePercent();

        //recommend not returning any orders if you are return them online
        if (returnPercent > 0) {
            rcr.setReturnClothing(true);
            rcr.setReturnTSaved(returnT);

            if (returnOnlinePercent != 100) {//if user is not already doing 100% of returns online
                rcr.setReturnOnline(true);
                //online orders are 60% more efficient(Thred up) therefore returning all orders online would reduce by 60%
                rcr.setReturnOnlineTSaved(returnT * .4);//the amount you would save by doing 100% of returns online
            }
        }
        return rcr;
    }

    private void loadFootprintViews(Footprint footprint) {
        flightCO2 = footprint.getFlight();
        clothingCO2 = footprint.getClothing();
        foodCO2 = footprint.getFood();
        totalCO2 = foodCO2 + flightCO2 + clothingCO2;
        totalCO2TextView.setText("Annual CO2 output:\n" + String.format("%.2f", totalCO2) + " Tonnes");
        userFootprint = footprint;//doesnt do anything
        setUpPieChart();
        loadPieChartData();
        setUpBarChart();
        loadBarChartData();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load generic home page

        totalCO2TextView = view.findViewById(R.id.totalCO2);
        pieChart = view.findViewById(R.id.pieChart);
        pieChartLayout = view.findViewById(R.id.pieChartLayout);
        foodPieChartLayout = view.findViewById(R.id.foodPieChartLayout);
        foodPieChart= view.findViewById(R.id.foodPieChart);
        barChart = view.findViewById(R.id.barChart);
        barChartTitle = view.findViewById(R.id.barChartTitle);
        barChartLayout = view.findViewById(R.id.barChartLayout);
        recLayout = view.findViewById(R.id.recommendationLayout);
        recTitle = view.findViewById(R.id.recommendationTitle);
        recParagraph = view.findViewById(R.id.recommendationParagraph);
        big3Layout = view.findViewById(R.id.big3Layout);
        
    }


    private void readData(FirebaseCallback firebaseCallback) {

        mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    userHasData=true;
                    firebaseCallback.onCallBack(footprint);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(getContext(), "Db error", Toast.LENGTH_SHORT).show();

            }
        });

        mDatabase.child(userID).child("clothing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clothing clothing = snapshot.getValue(Clothing.class);
                    //do whats required with user clothing
                    firebaseCallback.onCallBack(clothing);
                } else {
                    Log.i("in clothing", "Doesnt exist");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
            public void onCancelled(@NonNull DatabaseError error) {

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
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private interface FirebaseCallback {
        void onCallBack(Footprint footprint);

        void onCallBack(Clothing clothing);

        void onCallBack(List<Flight> flightList);

        void onCallBack(Food food);
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
        if (userHasData) {
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


        BarDataSet dataSet = new BarDataSet(entries, "You vs Average");
        dataSet.setColors(colors);

        BarData data = new BarData(dataSet);
        data.setDrawValues(true);
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(data);
        barChart.invalidate();
    }

    public void setUpPieChart() {
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

    public void loadPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) (flightCO2), "Flights"));
        entries.add(new PieEntry((float) (foodCO2), "Food"));
        entries.add(new PieEntry((float) (clothingCO2), "Clothing"));


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
        switch (String.valueOf(h.getX())) {
            case "0.0":
                //flight clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Flights");
                recParagraph.setText(flightRecContent);
                Log.i("I clicked on flight", String.valueOf(h.getX()));
                break;
            case "1.0":
                //food clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Food");
                recParagraph.setText(foodRecContent);
                foodPieChartLayout.setVisibility(View.VISIBLE);
                Log.i("I clicked on food", String.valueOf(h.getX()));
                break;
            case "2.0":
                //clothing clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Clothing");
                recParagraph.setText(clothingRecContent);
                Log.i("I clicked on clothing", String.valueOf(h.getX()));
                break;

        }


    }

    @Override
    public void onNothingSelected() {
        recLayout.setVisibility(View.GONE);
        if(foodPieChartLayout.getVisibility() == View.VISIBLE){
            foodPieChartLayout.setVisibility(View.GONE);
        }
        Log.i("nothing selected", "a");
        //set information invisible

    }

    private boolean ambitious(Footprint f) {
        return f.getAmbition().equalsIgnoreCase("High");
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

    public boolean biggestOfThree(double max, double val1, double val2) {
        //takes three doubles and returns true if the first double iss the largest of the three
        return max > val1 && max > val2;
    }

    public boolean biggestOfFive(double max, double val1, double val2, double val3, double val4) {
        Log.i("12345", String.valueOf(max)+"|"+String.valueOf(val1)+"|"+String.valueOf(val2)+"|"+String.valueOf(val3)+"|"+String.valueOf(val4));
        return max > val1 && max > val2 && max > val3 && max > val4;
    }

    private void getNewFlightFootprint(String depart,String arrive,boolean returnFlight) {
        Thread thread = new Thread(new Runnable() {// create a new thread to place the api call inside to prevent async errors
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                try {
                    //set up api call
                    URL url = new URL("https://api.goclimate.com/v1/flight_footprint?segments[0][origin]=" + getIATA(depart) + "&segments[0][destination]=" + getIATA(arrive) + "&cabin_class=economy");//can hardcode class as economy because this will only be used to convert business and first class flights
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");
                    conn.setRequestProperty("Authorization",
                            "Basic " + Base64.getEncoder().encodeToString((goClimateApiKey + ":").getBytes() //the api key for GoClimate
                            )
                    );
                    //handle api response
                    if (conn.getResponseCode() == 200 || conn.getResponseCode() == 200) {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }

                        JSONObject jsonResponse = new JSONObject(stringBuilder.toString());
                        double jsonFootprint = jsonResponse.getInt("footprint");
                        double newFootprint = jsonFootprint / 1000;//footprint is returned in kg therefore divide it by 1000
                        if (returnFlight) {
                            newFootprint = newFootprint * 2;
                        }
                       // Log.i("!!!!!newFF", String.valueOf(newFootprint));
                       // Log.i("!!!!!newFF3", String.valueOf(newFlightFootprint));

                            newFlightFootprint=newFootprint;
                        latch.countDown();
                      //  Log.i("!!!!!newFF4", String.valueOf(newFlightFootprint));//this is working
                        //need to send this new footprint value to the getFlightRec method
                    } else {

                        Log.e("URL : ", String.valueOf(url));
                        Log.e("Resonse code: ", String.valueOf(conn.getResponseCode()));
                        Log.e("Resonse message: ", String.valueOf(conn.getResponseMessage()));
                    }
                    conn.disconnect();

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }finally{
                    latch.countDown();
                }
            }

        });
        thread.start();

    }

    private String getIATA(String s) {
        return s.substring(s.length() - 4, s.length() - 1);
    }

}