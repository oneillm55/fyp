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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.fyp.ClothingFolder.Clothing;
import com.example.fyp.FlightFolder.Flight;
import com.example.fyp.FoodFolder.Food;
import com.example.fyp.RecommendationsFolder.ClothingRecs.PurchasingRec;
import com.example.fyp.RecommendationsFolder.ClothingRecs.ReturnClothingRec;
import com.example.fyp.RecommendationsFolder.ClothingRecs.WashingRec;
import com.example.fyp.RecommendationsFolder.FlightRecs.FlightClassRec;
import com.example.fyp.RecommendationsFolder.FlightRecs.FlightDistanceRec;
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private PieChart pieChart, foodPieChart,flightPieChart,clothingPieChart;
    private BarChart barChart;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase;
    private double foodCO2, flightCO2, clothingCO2, totalCO2, newFlightFootprint,meatCO2, shoppingCO2, dairyCO2, compostCO2,organicCO2 ;
    private TextView totalCO2TextView, recTitle, recParagraph, barChartTitle, clickPCTextView,flightRecParagraph,foodRecParagraph,clothingRecParagraph,barchartParagraph;
    private LinearLayout pieChartLayout,foodPieChartLayout,flightPieChartLayout,clothingPieChartLayout, barChartLayout, recLayout, big3Layout;
    private boolean userHasData;
    private ArrayList<Integer> colors,pieChartColors,flightChartColors,clothingChartColors;
    private String userID, clothingRecContent="", foodRecContent="", flightRecContent="",clothingContent="", foodContent="", flightContent="", goClimateApiKey;
    Footprint userFootprint = new Footprint();
    CountDownLatch latch = new CountDownLatch(1);


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

                hideDefaultViews();
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
                mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                            Footprint footprint = snapshot.getValue(Footprint.class);


                            loadFlightViews(flightList,footprint);
                        }
                    }
                    @Override
                    public void onCancelled (@NonNull DatabaseError error){

                    }
                });

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

    private void hideDefaultViews() {
        big3Layout.setVisibility(View.GONE);
    }

    private void loadFlightViews(List<Flight> flightList,Footprint footprint) {
        FlightClassRec flightClassRec = getFlightClassRec(flightList);
        FlightDistanceRec shortFlightRec = getFlightRec(flightList,"Short");
        FlightDistanceRec mediumFlightRec = getFlightRec(flightList,"Medium");
        FlightDistanceRec longFlightRec = getFlightRec(flightList,"Long");

        setFlightRec(flightList,flightClassRec,shortFlightRec,mediumFlightRec,longFlightRec,footprint);
        flightRecParagraph.setText(flightRecContent);
        loadFlightPieChartData(shortFlightRec,mediumFlightRec,longFlightRec);
        setUpFlightPieChart(footprint);
    }

    private void loadFlightPieChartData(FlightDistanceRec sfr,FlightDistanceRec mfr,FlightDistanceRec lfr) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) (round(sfr.getCost())), "Short"));
        entries.add(new PieEntry((float) (round(mfr.getCost())), "Medium"));
        entries.add(new PieEntry((float) (round(lfr.getCost())), "Long"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(flightChartColors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(flightPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        flightPieChart.setData(data);
        flightPieChart.invalidate();
    }

    private void setUpFlightPieChart(Footprint footprint) {
        flightPieChartLayout.setVisibility(View.VISIBLE);
        flightPieChart.setDrawHoleEnabled(true);
        flightPieChart.setUsePercentValues(true);
        flightPieChart.setDrawEntryLabels(true);
        flightPieChart.setEntryLabelTextSize(12);
        flightPieChart.setEntryLabelColor(Color.BLACK);
        flightPieChart.setCenterText("Flights\n"+round(footprint.getFlight())+"t");
        flightPieChart.setCenterTextSize(20);
        flightPieChart.getDescription().setEnabled(false);
        flightPieChart.setDrawEntryLabels(false);
        flightPieChart.setTouchEnabled(false);

        Legend l = flightPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(10);
        l.setEnabled(true);
    }


    private FlightDistanceRec getFlightRec(List<Flight> flightList,String haul) {
        FlightDistanceRec flightRec = new FlightDistanceRec();
        double flightTotal=0;
        double currentTotal=0;
        int count=0;
        for(Flight flight : flightList){
            currentTotal+=flight.getFootprint();
            if(flight.getDistance().equalsIgnoreCase(haul)){
                flightTotal+=flight.getFootprint();
                count++;
            }
        }
        flightRec.setCount(count);
        flightRec.setCost(flightTotal);
        flightRec.setTSavedAmbitious(currentTotal-flightTotal);
        flightRec.setPercent(round(flightTotal/currentTotal)*100);
        return flightRec;
    }

    private void setFlightRec(List<Flight> flightList, FlightClassRec flightClassRec,FlightDistanceRec shortFlightRec,FlightDistanceRec mediumFlightRec,FlightDistanceRec longFlightRec,Footprint footprint) {
        Log.i("sml dif", String.valueOf(shortFlightRec.getPercent())+"|"+String.valueOf(mediumFlightRec.getPercent())+"|"+String.valueOf(longFlightRec.getPercent()));
        if(flightClassRec.getTSaved()>0) {
            //Flight class recommendation is always shown if applicable to the user
            flightRecContent = "-By flying economy on all your flights you would save " + String.valueOf(round(flightClassRec.getTSaved())+"t of CO2.\nConsider this a little less comfort for a lot less carbon.\n");
        }

        if(biggestOfThree(shortFlightRec.getPercent(),mediumFlightRec.getPercent(),longFlightRec.getPercent())){

            reduceShortHaul(shortFlightRec,footprint);

        }else if (biggestOfThree(mediumFlightRec.getPercent(),shortFlightRec.getPercent(),longFlightRec.getPercent())){
            reduceMediumHaul(mediumFlightRec,footprint);

        }else if (biggestOfThree(longFlightRec.getPercent(),shortFlightRec.getPercent(),mediumFlightRec.getPercent())){
            reduceLongHaul(longFlightRec,footprint);

        }else{
            //in general long haul flights produce more than medium produce more than short so in the rare event two or more distance values are equal this is a safe recommendation
            if(longFlightRec.getCost()>0){
                reduceLongHaul(longFlightRec,footprint);
            }else if(mediumFlightRec.getCost()>0){

                reduceMediumHaul(mediumFlightRec,footprint);
            }else{
                reduceShortHaul(shortFlightRec,footprint);
            }



        }
    }

    private void reduceShortHaul(FlightDistanceRec shortFlightRec, Footprint footprint) {
        flightContent +=getFlightPercentString("Short",shortFlightRec);
        if(shortFlightRec.getCount()==1 || ambitious(footprint)){
            flightRecContent +="-Commit to cutting out short haul flights and save "+round(shortFlightRec.getCost())+"t of CO2, "+round(shortFlightRec.getCost()/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringMonths(round((shortFlightRec.getCost()/0.0275)));
        }else{
            //user is not ambitious and has more than one short haul flight
            flightRecContent +="-By reducing your short haul flights by 50% you could cut out "+round(shortFlightRec.getCost()*.5)+"t of CO2, "+round((shortFlightRec.getCost()*.5)/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringMonths(round((shortFlightRec.getCost()*.5)/0.0275));
        }
    }

    private void reduceMediumHaul(FlightDistanceRec mediumFlightRec, Footprint footprint) {
        flightContent +=getFlightPercentString("Medium",mediumFlightRec);
        if(mediumFlightRec.getCount()==1 || ambitious(footprint)){
            flightRecContent +="-Commit to cutting out medium haul flights and save "+round(mediumFlightRec.getCost())+"t of CO2, "+round(mediumFlightRec.getCost()/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringMonths(round((mediumFlightRec.getCost()/0.0275)));
        }else{
            //user is not ambitious and has more than one short medium flight
            flightRecContent +="-By reducing your medium haul flights by 50% you could cut out "+round(mediumFlightRec.getCost()*.5)+"t of CO2, "+round((mediumFlightRec.getCost()*.5)/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringMonths(round((mediumFlightRec.getCost()*.5)/0.0275));
        }
    }

    private void reduceLongHaul(FlightDistanceRec longFlightRec, Footprint footprint) {
        flightContent +=getFlightPercentString("Long",longFlightRec);
        if(longFlightRec.getCount()==1 || ambitious(footprint)){
            flightRecContent +="-Commit to cutting out long haul flights and save "+round(longFlightRec.getCost())+"t of CO2, "+round(longFlightRec.getCost()/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringYears(round(longFlightRec.getCost()/0.33));
        }else{
            //user is not ambitious and has more than one long haul flight
            flightRecContent +="-By reducing your long haul flights by 50% you could cut out "+round(longFlightRec.getCost()*.5)+"t of CO2, "+round((longFlightRec.getCost()*.5)/footprint.getFlight())*100+"% of your total flight footprint!\n";
            flightRecContent += getVegetarianStringYears(round((longFlightRec.getCost()*.5)/0.33));
        }
    }

    private String getFlightPercentString(String string, FlightDistanceRec flightRec) {
        return string+" haul flights make up "+round(flightRec.getPercent())+"% of your flight footprint and produce "+round(flightRec.getCost())+"t of CO2!\n";
    }

    private String getVegetarianStringYears(double d){
        return "That would save as much carbon as a meat eater going vegetarian for "+d+" years!";

    }

    private String getVegetarianStringMonths(double d){
        return "That would save as much carbon as a meat eater going vegetarian for "+d+" months!";

    }

    private FlightClassRec getFlightClassRec(List<Flight> flightList) {
        FlightClassRec fcr = new FlightClassRec();
        double newTotal=0;//stores the new flight total if the user took all their flights economy
        double currentTotal=0;// the users current flight footprint
       for(Flight flight : flightList){
           currentTotal+=flight.getFootprint();//get the current total footprint of all flights
           if(flight.getFlightClass().trim().equalsIgnoreCase("Economy")){
               newTotal+=flight.getFootprint();
           }else{
               getNewFlightFootprint(flight.getDepart(), flight.getArrive(), flight.isReturnFlight());//this will set newFlight footprint to be the value of the current flight if flown economy
               try {
                   latch.await();//wait for newFlightFootprint to be assigned
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
               newTotal+=newFlightFootprint;
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
        setUpFoodPieChart(footprint);
        loadFoodPieChartData();
        MeatRec meatRec = getMeatRec(food, footprint);
        DairyRec dairyRec = getDairyRec(food,footprint);
        ShoppingRec shoppingRec = getShoppingRec(food,footprint);
        CompostRec compostRec = getCompostRec(food);
        OrganicRec organicRec = getOrganicRec(food,footprint);

        setFoodRec(food, meatRec, dairyRec, shoppingRec, compostRec, organicRec);
        foodRecParagraph.setText(foodRecContent);
    }


    private void loadFoodPieChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) (meatCO2), "Meat"));
        entries.add(new PieEntry((float) (dairyCO2), "Dairy"));
        entries.add(new PieEntry((float) (shoppingCO2), "Shopping"));
        entries.add(new PieEntry((float) (compostCO2), "Compost"));
        entries.add(new PieEntry((float) (organicCO2), "Organic"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(pieChartColors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(foodPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        foodPieChart.setData(data);
        foodPieChart.invalidate();
    }

    private void setUpFoodPieChart(Footprint footprint) {
        foodPieChartLayout.setVisibility(View.VISIBLE);
        foodPieChart.setDrawHoleEnabled(true);
        foodPieChart.setUsePercentValues(true);
        foodPieChart.setDrawEntryLabels(true);
        foodPieChart.setEntryLabelTextSize(12);
        foodPieChart.setEntryLabelColor(Color.BLACK);
        foodPieChart.setCenterText("Food\n"+round(footprint.getFood())+"t");
        foodPieChart.setCenterTextSize(20);
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
        if (biggestOfFive(meat, dairy, shopping, compost, organic)) {;
            foodContent=getFoodContent("Meat",round(food.getMeatValue()));
            foodRecContent=getFoodRecContent("Meat",meatRec.getSuggestion(),round(meat));
        } else if (biggestOfFive(dairy, shopping, compost, organic, meat)) {
            foodContent=getFoodContent("Dairy",round(food.getDairyValue()));
            foodRecContent=getFoodRecContent("Dairy",dairyRec.getSuggestion(),round(dairy));
        } else if (biggestOfFive(shopping, compost, organic, meat, dairy)) {
            foodContent=getFoodContent("Shopping",round(food.getShoppingValue()));
            foodRecContent=getFoodRecContent("Shopping",shoppingRec.getSuggestion(),round(shopping));
        } else if (biggestOfFive(compost, organic, meat, dairy, shopping)) {
            foodContent=getFoodContent("Not composting",round(food.getCompostValue()));
            foodRecContent="By composting your food you could save"+ round(compost)+"t of CO2";
        } else if(biggestOfFive(organic, meat, dairy, shopping,compost)) {
            foodContent=getFoodContent("Not eating enough organic food",round(food.getOrganicValue()));
            foodRecContent="By changing the amount of organic food you eat to \""+organicRec.getSuggestion()+"\"you could save"+ round(organic)+"t of CO2";
        }else{
                //two values are equal therefore display recommendations based on general order of the largest CO2 contributors
            if (meat>0) {;
                foodContent=getFoodContent("Meat",round(food.getMeatValue()));
                foodRecContent=getFoodRecContent("Meat",meatRec.getSuggestion(),round(meat));
            }  else if (shopping>0) {
                foodContent=getFoodContent("Shopping",round(food.getShoppingValue()));
                foodRecContent=getFoodRecContent("Shopping",shoppingRec.getSuggestion(),round(shopping));
            } else if (dairy>0) {
                foodContent=getFoodContent("Dairy",round(food.getDairyValue()));
                foodRecContent=getFoodRecContent("Dairy",dairyRec.getSuggestion(),round(dairy));
            }else if (compost>0) {
                foodContent=getFoodContent("Not composting",round(food.getCompostValue()));
                foodRecContent="By composting your food you could save"+ round(compost)+"t of CO2";
            } else {
                foodContent=getFoodContent("Not eating enough organic food",round(food.getOrganicValue()));
                foodRecContent="By changing the amount of organic food you eat to \""+organicRec.getSuggestion()+"\"you could save"+ round(organic)+"t of CO2";;
            }
        }
    }

    private String getFoodRecContent(String string,String suggestion, double value) {
        return "By reducing your "+string+" consumption to:\""+suggestion+"\" you could save "+ value+"t of CO2";
    }

    private String getFoodContent(String string, double value) {
        return string+" is the biggest contributor to your food footprint producing "+ value+"t of CO2";
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
                break;
            case "Every day":
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
                meatTSaved = meatValue;
                suggestion = "Never";

                break;
            case "Once a day":
                if ((ambitious(footprint))) {
                    meatTSaved = meatValue;
                    suggestion = "Never";
                } else {
                    meatTSaved = meatValue - .08;
                    suggestion = "Occasionally";

                }
                break;
            case "Most meals":
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
        clothingRecParagraph.setText(clothingRecContent);
        loadClothingPieChartData(clothing);
        setUpClothingPieChart(footprint);
    }
    private void loadClothingPieChartData(Clothing clothing) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) (round(clothing.getReturnT())), "Returned Items"));
        entries.add(new PieEntry((float) (round(clothing.getColdWashT()+clothing.getAirDryT())), "Washing"));
        entries.add(new PieEntry((float) (round(clothing.getSustainableT()+clothing.getSecondHandT())), "Purchasing"));
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(clothingChartColors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(clothingPieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);

        clothingPieChart.setData(data);
        clothingPieChart.invalidate();
    }

    private void setUpClothingPieChart(Footprint footprint) {
        clothingPieChartLayout.setVisibility(View.VISIBLE);
        clothingPieChart.setDrawHoleEnabled(true);
        clothingPieChart.setUsePercentValues(true);
        clothingPieChart.setDrawEntryLabels(true);
        clothingPieChart.setEntryLabelTextSize(12);
        clothingPieChart.setEntryLabelColor(Color.BLACK);
        clothingPieChart.setCenterText("Clothing\n"+round(footprint.getClothing())+"t");
        clothingPieChart.setCenterTextSize(20);
        clothingPieChart.getDescription().setEnabled(false);
        clothingPieChart.setDrawEntryLabels(false);
        clothingPieChart.setTouchEnabled(false);

        Legend l = clothingPieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setTextSize(10);
        l.setEnabled(true);
    }

    private void setClothingRec(WashingRec washingRec, PurchasingRec purchasingRec, ReturnClothingRec returnClothingRec, Clothing clothing, Footprint footprint) {

        double washing = washingRec.getTotalWashingTSaved(), purchasing = purchasingRec.getTotalPurchasingTSaved(), returnClothing = returnClothingRec.getTotalReturnTSaved();
        if (biggestOfThree(washing, purchasing, returnClothing)) {

            double saved=0;
            clothingContent = "Your clothes washing habits are the biggest factor in your clothing footprint producing "+round(clothing.getAirDryT()+clothing.getColdWashT())+ "t of CO2\n";
            if (washingRec.isAirDry()) {
                if(ambitious(footprint)){
                    clothingRecContent += "\n-By air drying all your laundry, you could save " + round(washingRec.getAirDryTSaved()) + "t of CO2\n";
                    saved+=round(washingRec.getAirDryTSaved());
                }else{

                    clothingRecContent += "\n-By air drying 80% of your laundry, you could save  " + round(washingRec.getAirDryTSaved()*.8) + "t of CO2\n";
                    saved+=round(washingRec.getAirDryTSaved()*.8);
                }
            }

            if (washingRec.isColdWash()) {
                if(ambitious(footprint)){

                    clothingRecContent += "\n-By doing all your laundry on a cold wash you would save" + round(washingRec.getColdWashTSaved()) + "t of CO2\n";
                    saved+=round(washingRec.getAirDryTSaved());
                }else{

                    clothingRecContent += "\n-By doing 80% of your laundry on a cold wash you would save " + round(washingRec.getColdWashTSaved()*.8) + "t of CO2\n";
                    saved+=round(washingRec.getAirDryTSaved()*.8);
                }
            }
            clothingRecContent += getDubLonFlightsSaved(round(saved));

        } else if (biggestOfThree(purchasing, washing, returnClothing)) {
            double saved = 0;
            clothingContent = "The types of clothing yor buying(or not buying) are the biggest contributor to your clothing footprint producing "+round(clothing.getSustainableT()+clothing.getSecondHandT())+"t of CO2\n";

            if(purchasingRec.isSustainable()){
                clothingRecContent += "\n-By purchasing 50% more of your clothes from sustainable brands you could save" + round(purchasingRec.getSecondHandTSaved()) + "t of CO2\n";
                saved +=round(purchasingRec.getSecondHandTSaved());
            }

            if(purchasingRec.isSecondHand()){
                clothingRecContent += "\n-By purchasing 50% more of your clothes second hand you could save" + round(purchasingRec.getSecondHandTSaved()) + "t of CO2\n";
                saved +=round(purchasingRec.getSecondHandTSaved());
            }

            clothingRecContent += getDubLonFlightsSaved(round(saved));

        } else if (biggestOfThree(returnClothing, washing, purchasing)) {

            clothingContent = "By returning"+clothing.getReturnPercent()+"% of your clothes you're generating "+round(clothing.getReturnT())+"t of CO2\n";
            if(ambitious(footprint)){
                if(returnClothingRec.isReturnClothing()){
                    clothingRecContent += "If you didn't return any of your clothes you would save " + round(returnClothingRec.getReturnTSaved())+"t of CO2\n";
                }
            }else{
                if(returnClothingRec.isReturnClothing()) {
                    clothingRecContent += "We recommend cutting down the amount of clothes you return by 50% , saving " + String.valueOf(round(returnClothing * .5)) + "t of CO2\n";
                }
                if(returnClothingRec.isReturnOnline()){
                    clothingRecContent += "We recommend doing your returns online (rather than driving to the shop yourself) this would save " + round(returnClothingRec.getReturnTSaved())+"t of CO2\n";
                }
            }

        } else {
            //two of the values are equal
            clothingRecContent = "You could benefit from changing more than one habit regarding your clothing consumption ";
        }

    }

    private String getDubLonFlightsSaved(double saved) {
        double amountOfDubLonFlights = 0.2/saved;
        if(amountOfDubLonFlights>.5){

            return "That's the equivalent of  "+round(amountOfDubLonFlights)+" flights from Dublin to London!";
        }else {
            return "";
        }
    }

    private WashingRec getWashingRec(Clothing clothing,Footprint footprint) {

        double airDryT = clothing.getAirDryT(), coldwashT = clothing.getColdWashT(), savedT = 0, coldwashPercent = clothing.getColdWashPercent(), airDryPercent = clothing.getAirDryPercent();
        WashingRec wr = new WashingRec();
        if (coldwashPercent < 100) {//if user isnt currently at the recomended % of cold washes
            if(footprint.getAmbition().equalsIgnoreCase("High")){
                wr.setColdWashSuggestion("We recommend you do all your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT)+"t of CO2 annually");
            }else if (coldwashPercent < 80){

                wr.setColdWashSuggestion("We recommend you do 80% your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT*.8)+"t of CO2 annually");
            }else{
                //user has moderate ambition and is already cold washing between 80-99% of their clothes
            }
            wr.setColdWash(true);
            wr.setColdWashTSaved(coldwashT);
        }

        if (airDryPercent < 100) {
            if(footprint.getAmbition().equalsIgnoreCase("High")){
                wr.setColdWashSuggestion("We recommend you do all your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT)+"t of CO2 annually");
            }else if (airDryPercent < 80){

                wr.setColdWashSuggestion("We recommend you do 80% your laundry on a cold wash by doing so you could save"+String.valueOf(coldwashT*.8)+"t of CO2 annually");
            }else{
                //user has moderate ambition and is already air drying between 80-99% of their clothes
            }
            wr.setAirDry(true);
            wr.setAirDryTSaved(airDryT);
        }

        return wr;


    }

    private PurchasingRec getPurchasingRec(Clothing clothing) {
        PurchasingRec pr = new PurchasingRec();
        double secondHandT = clothing.getSecondHandT(), sustainableT = clothing.getSustainableT(), secondHandPercent = clothing.getSecondHandPercent(), sustainablePercent = clothing.getSustainablePercent();


        if (sustainablePercent < 50) {
            pr.setSustainable(true);

            double costFifty = 0.063276084;//cost of a user purchasing 50% of their clothes sustainably
            double sustainableTSaved = sustainableT - costFifty; //difference between users current cost and the cost of them purchasing 50% sustainably ie their potential savings
            pr.setSustainableTSaved(sustainableTSaved);
        }
        if (secondHandPercent < (sustainablePercent * 1.50)) {//user can purchase 50% more second hand
            //recommend more second hand
            double secondHandTSaved = secondHandT * .5;//amount they could save if they purchase 50% more second hand
            pr.setSecondHand(true);
            pr.setSecondHandTSaved(secondHandTSaved);
        }
        return pr;
    }

    private ReturnClothingRec getReturnClothingRec(Clothing clothing) {
        ReturnClothingRec rcr = new ReturnClothingRec();
        double returnT = clothing.getReturnT(), returnOnlineT = clothing.getReturnT(), returnPercent = clothing.getReturnPercent(), returnOnlinePercent = clothing.getReturnOnlinePercent();

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
        setUpPieChart(footprint);
        loadPieChartData();
        setUpBarChart();
        loadBarChartData(footprint);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pieChartColors = new ArrayList<>();
        pieChartColors.add(Color.parseColor("#FF0000"));
        pieChartColors.add(Color.parseColor("#FF7300"));
        pieChartColors.add(Color.parseColor("#52D726"));
        pieChartColors.add(Color.parseColor("#FFEC00"));
        pieChartColors.add(Color.parseColor("#007ED6"));

        flightChartColors = new ArrayList<>();
        flightChartColors.add(Color.parseColor("#72FFFF"));
        flightChartColors.add(Color.parseColor("#0096FF"));
        flightChartColors.add(Color.parseColor("#5800FF"));

        clothingChartColors = new ArrayList<>();
        clothingChartColors.add(Color.parseColor("#F6D860"));
        clothingChartColors.add(Color.parseColor("#FF7F3F"));
        clothingChartColors.add(Color.parseColor("#EA5C2B"));

        totalCO2TextView = view.findViewById(R.id.totalCO2);
        pieChart = view.findViewById(R.id.pieChart);
        pieChartLayout = view.findViewById(R.id.pieChartLayout);
        foodPieChartLayout = view.findViewById(R.id.foodPieChartLayout);
        foodPieChart= view.findViewById(R.id.foodPieChart);
        flightPieChartLayout = view.findViewById(R.id.flightPieChartLayout);
        flightPieChart= view.findViewById(R.id.flightPieChart);
        clothingPieChartLayout = view.findViewById(R.id.clothingPieChartLayout);
        clothingPieChart= view.findViewById(R.id.clothingPieChart);
        barChart = view.findViewById(R.id.barChart);
        barChartTitle = view.findViewById(R.id.barChartTitle);
        barChartLayout = view.findViewById(R.id.barChartLayout);
        recLayout = view.findViewById(R.id.recLayout);
        recTitle = view.findViewById(R.id.recommendationTitle);
        recParagraph = view.findViewById(R.id.recommendationParagraph);
        flightRecParagraph = view.findViewById(R.id.flightRecommendationParagraph);
        foodRecParagraph = view.findViewById(R.id.foodRecommendationParagraph);
        clothingRecParagraph = view.findViewById(R.id.clothingRecommendationParagraph);
        barchartParagraph = view.findViewById(R.id.barChartParagraph);
        big3Layout = view.findViewById(R.id.big3Layout);
        clickPCTextView = view.findViewById(R.id.clickPCTextView);
        
    }


    private void readData(FirebaseCallback firebaseCallback) {

        mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    userHasData=true;
                    firebaseCallback.onCallBack(footprint);
                }else{
                    loadDefaultViews();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mDatabase.child(userID).child("clothing").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Clothing clothing = snapshot.getValue(Clothing.class);
                    firebaseCallback.onCallBack(clothing);
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

    private void loadDefaultViews() {

        big3Layout.setVisibility(View.VISIBLE);
        setUpBarChart();
        Footprint footprint = new Footprint();
        loadBarChartData(footprint);

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

    private void loadBarChartData(Footprint footprint) {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (userHasData) {
            labels.add("You");
            entries.add(new BarEntry(0, (float) totalCO2));
            colors.add(Color.rgb(26, 201, 53));
            barChartTitle.setText("You vs Rest of World");
            barchartParagraph.setText(getBarChartParagraph(footprint));
        }else{
            barChartTitle.setText("Ireland vs Rest of World");
        }

        labels.add("Ireland");
        labels.add("EU");
        labels.add("UK");
        labels.add("US");
        labels.add("Worldwide");


        entries.add(new BarEntry(1, 12.8F));
        entries.add(new BarEntry(2, 8.8F));
        entries.add(new BarEntry(3, 8.3F));
        entries.add(new BarEntry(4, 15.5F));
        entries.add(new BarEntry(5, 4.5F));

        colors.add(Color.rgb(19, 135, 37));
        colors.add(Color.rgb(19, 35, 137));
        colors.add(Color.rgb(19, 35, 137));
        colors.add(Color.rgb(19, 35, 137));
        colors.add(Color.rgb(19, 35, 137));
        colors.add(Color.rgb(19, 35, 137));


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

    private String getBarChartParagraph(Footprint footprint) {
        String s="";
        double total = footprint.getClothing()+footprint.getFlight()+footprint.getFood();
        if(total>13){
            s="Your footprint is bigger than most Irish people and we're not doing well to begin with!\nYou could really benefit from some of the recommendations listed below, take a look now to start your journey toward a 5 tonne lifestyle.";
        }else if(total<5.5) {
            s="Your footprint is well below the average Irish person, well done!\nHowever you can still improve it, take a look at some of our recommendations below";
        } else{
            s="Your footprint could use some work!\n take a look at some of our recommendations below to start your journey toward a 5 tonne lifestyle.";
            }

        return s;
    }

    public void setUpPieChart(Footprint footprint) {
        pieChartLayout.setVisibility(View.VISIBLE);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Annual CO2\n"+round(footprint.getClothing()+footprint.getFlight()+footprint.getFood())+"t");
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
        clickPCTextView.setVisibility(View.GONE);
        switch (String.valueOf(h.getX())) {
            case "0.0":
                //flight clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Flights");
                recParagraph.setText(flightContent);
                break;
            case "1.0":
                //food clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Food");
                recParagraph.setText(foodContent);
                break;
            case "2.0":
                //clothing clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Clothing");
                recParagraph.setText(clothingContent);
                break;

        }


    }

    @Override
    public void onNothingSelected() {
        recLayout.setVisibility(View.GONE);
        Log.i("nothing selected", "a");

    }

    private boolean ambitious(Footprint f) {
        return f.getAmbition().equalsIgnoreCase("High");
    }


    public double round(double number) {
        number = Math.round(number * 100);
        number = number/100;
        return number;
    }

    public boolean biggestOfThree(double max, double val1, double val2) {
        //takes three doubles and returns true if the first double iss the largest of the three
        return max > val1 && max > val2;
    }

    public boolean biggestOfFive(double max, double val1, double val2, double val3, double val4) {
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

                            newFlightFootprint=newFootprint;
                        latch.countDown();
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