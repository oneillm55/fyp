package com.example.fyp.RecommendationsFolder.FoodRecs;

public class ShoppingRec {
    double shoppingTSaved;
    String suggestion;

    public ShoppingRec() {
    }

    public ShoppingRec(double shoppingTSaved, String suggestion) {
        this.shoppingTSaved = shoppingTSaved;
        this.suggestion = suggestion;
    }

    public double getShoppingTSaved() {
        return shoppingTSaved;
    }

    public void setShoppingTSaved(double shoppingTSaved) {
        this.shoppingTSaved = shoppingTSaved;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }
}
