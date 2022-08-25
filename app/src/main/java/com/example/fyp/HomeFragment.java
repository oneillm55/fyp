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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private PieChart pieChart;
    private BarChart barChart;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference mDatabase, footprintReference;
    private double foodCO2, flightCO2, clothingCO2, totalCO2;
    private TextView totalCO2TextView, recTitle, recParagraph, barChartTitle;
    private LinearLayout pieChartLayout, barChartLayout, recLayout, big3Layout;
    private boolean userHasData;
    private ArrayList<Integer> colors;
    private String userID, clothingRecContent, foodRecContent;
    private String level="Conservative";
    private Button recLevelButton;
    Footprint userFootprint = new Footprint();
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


        readData(new FirebaseCallback() {
            @Override
            public void onCallBack(Footprint footprint) {
                loadFootprintViews(footprint);
            }

            @Override
            public void onCallBack(Clothing clothing) {
//                // a way for the app to use both the clothing and the footprint at once
//                mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
//                            Footprint footprint = snapshot.getValue(Footprint.class);
//
//                            loadClothingViews(clothing);
//                            WashingRec washingRec= washing(clothing);
//                            PurchasingRec purchasingRec = purchasing(clothing);
//                            ReturnClothingRec returnClothingRec = returnClothing(clothing);
//
//                            setClothingRec(washingRec,purchasingRec,returnClothingRec, clothing);
//
//                            //if footprint.get largest element is clothing set clothing rec to be at top of screen else set them to be in the rec paragraph for clothing
//                        }
//                    }
//                    @Override
//                    public void onCancelled (@NonNull DatabaseError error){
//
//                    }
//                });
                loadClothingViews(clothing);
            }

            @Override
            public void onCallBack(List<Flight> flightList) {

            }

            @Override
            public void onCallBack(Food food) {
                loadFoodViews(food);

            }
        });

        // colors array has to be done in on create as it uses getResources()
        colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.blue_jeans));
        colors.add(getResources().getColor(R.color.cyber_yellow));
        colors.add(getResources().getColor(R.color.heat_wave));
        return inflater.inflate(R.layout.fragment_home, container, false);


    }

    private void loadFoodViews(Food food) {
        MeatRec meatRec = getMeatRec(food);
        DairyRec dairyRec = getDairyRec(food);
        ShoppingRec shoppingRec = getShoppingRec(food);
        CompostRec compostRec = getCompostRec(food);
        OrganicRec organicRec = getOrganicRec(food);

        setFoodRec(food, meatRec, dairyRec, shoppingRec, compostRec, organicRec);
    }

    private void setFoodRec(Food food, MeatRec meatRec, DairyRec dairyRec, ShoppingRec shoppingRec, CompostRec compostRec, OrganicRec organicRec) {
        //set the values on the homepage with the recomendations that correspond to the passed parameters
        double meat = meatRec.getMeatTSaved(), dairy = dairyRec.getDairyTSaved(), shopping = shoppingRec.getShoppingTSaved(), compost = compostRec.getCompostTSaved(), organic = organicRec.getOrganicTSaved();
        if (biggestOfFive(meat, dairy, shopping, compost, organic)) {
            foodRecContent="Biggest saving= meat\n Save"+ meatRec.getMeatTSaved();
        } else if (biggestOfFive(dairy, shopping, compost, organic, meat)) {
            foodRecContent="Biggest saving= dairy\n Save"+ dairyRec.getDairyTSaved();
        } else if (biggestOfFive(shopping, compost, organic, meat, dairy)) {
            foodRecContent="Biggest saving= shopping\n Save"+ shoppingRec.getShoppingTSaved();
        } else if (biggestOfFive(compost, organic, meat, dairy, shopping)) {
            foodRecContent="Biggest saving= compost\n Save"+ compostRec.getCompostTSaved();
        } else if(biggestOfFive(organic, meat, dairy, shopping,compost)) {
            foodRecContent="Biggest saving= organic\n Save"+ organicRec.getOrganicTSaved();
        }else{
                //two values are equal
            //get the two equal values and display the recomendation for the one with the larger overall output
        }
    }

    private OrganicRec getOrganicRec(Food food) {
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
                if (serious()) {
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
        return new OrganicRec(organicTSaved, organicString);

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
        cr.setCompostTSaved(compostTSaved);
        cr.setSuggestion(suggestion);
        return cr;
    }

    private ShoppingRec getShoppingRec(Food food) {
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
                shoppingTSaved = shoppingValue - 0.3;
                suggestion = "Only local produce";

                break;
            case "Mostly supermarkets, try to buy Irish":
                if (serious()) {
                    shoppingTSaved = shoppingValue - 0.3;
                    suggestion = "Only local produce";
                } else {
                    shoppingTSaved = shoppingValue - 0.7;
                    suggestion = "Mostly Local, some supermarkets";
                }
                break;
            case "All supermarkets, pay no attention to country of origin":
                if (serious()) {
                    shoppingTSaved = shoppingValue - 0.3;
                    suggestion = "Only local produce";
                } else {
                    shoppingTSaved = shoppingValue - 0.15;
                    suggestion = "Mostly supermarkets, try to buy Irish";
                }
                break;
        }
        sr.setSuggestion(suggestion);
        sr.setShoppingTSaved(shoppingTSaved);
        return sr;
    }

    private DairyRec getDairyRec(Food food) {
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
                if (serious()) {
                    dairyTSaved = dairyValue;
                    suggestion = "Never";

                } else {
                    dairyTSaved = dairyValue - .04;
                    suggestion = "Occasionally";
                }
                break;
        }

        dr.setSuggestion(suggestion);
        dr.setDairyTSaved(dairyTSaved);
        return dr;
    }

    private MeatRec getMeatRec(Food food) {
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
                //user is eating meat once a day if they are serious they might consider removing meat from diet else reccomend occasionally
                if (serious()) {
                    meatTSaved = meatValue;
                    suggestion = "Never";
                } else {
                    meatTSaved = meatValue - .08;
                    suggestion = "Occasionally";

                }
                break;
            case "Most meals":
                //if you ate once a day instead
                if (serious()) {

                    meatTSaved = meatValue - .08;
                    suggestion = "Occasionally";
                } else {
                    meatTSaved = meatValue - .21;
                    suggestion = "Once a day";
                }
                break;
        }
        mr.setMeatTSaved(meatTSaved);
        mr.setSuggestion(suggestion);
        return mr;
    }

    private void loadClothingViews(Clothing clothing) {
        WashingRec washingRec = getWashingRec(clothing);
        PurchasingRec purchasingRec = getPurchasingRec(clothing);
        ReturnClothingRec returnClothingRec = getReturnClothingRec(clothing);

        setClothingRec(washingRec, purchasingRec, returnClothingRec, clothing);
    }

    private void setClothingRec(WashingRec washingRec, PurchasingRec purchasingRec, ReturnClothingRec returnClothingRec, Clothing clothing) {

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

    private WashingRec getWashingRec(Clothing clothing) {

        double airDryT = clothing.getAirDryT(), coldwashT = clothing.getColdWashT(), savedT = 0, coldwashPercent = clothing.getColdWashPercent(), airDryPercent = clothing.getAirDryPercent();
        WashingRec wr = new WashingRec();
        //Reccomendation is to cold wash 100% of clothes and air dry 100%
        // if((coldwashPercent*1.50)<100){//if user isnt currently at the recomended % of cold washesif its possible to wash 50% more on cold
        if (coldwashPercent < 100) {//if user isnt currently at the recomended % of cold washes
            //reccomend cold washing more
            wr.setColdWash(true);
            wr.setColdWashTSaved(coldwashT);
        }

        if (airDryPercent < 100) {
            //reccomend airdry more
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
        barChart = view.findViewById(R.id.barChart);
        barChartTitle = view.findViewById(R.id.barChartTitle);
        barChartLayout = view.findViewById(R.id.barChartLayout);
        recLayout = view.findViewById(R.id.recommendationLayout);
        recTitle = view.findViewById(R.id.recommendationTitle);
        recParagraph = view.findViewById(R.id.recommendationParagraph);
        big3Layout = view.findViewById(R.id.big3Layout);
        recLevelButton = view.findViewById(R.id.recLevelButton);
        recLevelButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  //todo reset the recommendations paragraphs
                                                  //move this to the footprint db
                                                  if (level.equalsIgnoreCase("Serious")) {
                                                      level = "Conservative";
                                                      recLevelButton.setText("Recomendations: Conservative");
                                                  } else {
                                                      level = "Serious";
                                                      recLevelButton.setText("Recomendations:Serious");
                                                  }
                                              }
                                          }
        );
        //    Footprint userFootprint = ((MyApplication) getActivity().getApplication()).getUserFootprint();
        //   Log.i("!!!!!", String.valueOf(userFootprint.getFlight()));
        //get user data
    }

    public void setUserFootprint() {
        mDatabase.child("footprint").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
                    //userFootprint =footprint;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Db error", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void readData(FirebaseCallback firebaseCallback) {

        mDatabase.child(userID).child("footprint").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {//check that user has a flight/ food / clothing footprint value
                    Footprint footprint = snapshot.getValue(Footprint.class);
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
                    Log.i("in clothing", String.valueOf(clothing.getAirDryT()));

                    Toast.makeText(getContext(), "in clothing" + String.valueOf(clothing.getAirDryT()), Toast.LENGTH_SHORT).show();
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

    private void setBig3(double d) {
        //go to the database, find the 3 biggest culprits for users footprint, set the big three to be those 3 in order of tonnes of co2 saved
        Toast.makeText(getContext(), "d" + String.valueOf(d), Toast.LENGTH_SHORT).show();

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
                recParagraph.setText("fly less lad");
                Log.i("I clicked on flight", String.valueOf(h.getX()));
                break;
            case "1.0":
                //food clicked
                recLayout.setVisibility(View.VISIBLE);
                recTitle.setText("Food");
                recParagraph.setText(foodRecContent);
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
        Log.i("nothing selected", "a");
        //set information invisible

    }

    private boolean serious() {
        return level.equalsIgnoreCase("Serious");
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
        return max > val1 && max > val2 && max > val3 && max > val4;
    }

}