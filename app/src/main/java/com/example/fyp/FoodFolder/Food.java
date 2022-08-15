package com.example.fyp.FoodFolder;

public class Food {
    String meatString,dairyString,compostString, organicString, shoppingString;
    double meatValue, dairyValue,  compostValue,organicValue,shoppingValue, foodValue;

    public Food(String meatString, String dairyString, String compostString, String organicString, String shoppingString, double meatValue, double dairyValue, double compostValue, double organicValue, double shoppingValue, double foodValue) {
        this.meatString = meatString;
        this.dairyString = dairyString;
        this.compostString = compostString;
        this.organicString = organicString;
        this.shoppingString = shoppingString;
        this.meatValue = meatValue;
        this.dairyValue = dairyValue;
        this.compostValue = compostValue;
        this.organicValue = organicValue;
        this.shoppingValue = shoppingValue;
        this.foodValue = foodValue;
    }

    public Food() {

    }

    public String getMeatString() {
        return meatString;
    }

    public void setMeatString(String meatString) {
        this.meatString = meatString;
    }

    public String getDairyString() {
        return dairyString;
    }

    public void setDairyString(String dairyString) {
        this.dairyString = dairyString;
    }

    public String getCompostString() {
        return compostString;
    }

    public void setCompostString(String compostString) {
        this.compostString = compostString;
    }

    public String getOrganicString() {
        return organicString;
    }

    public void setOrganicString(String organicString) {
        this.organicString = organicString;
    }

    public String getShoppingString() {
        return shoppingString;
    }

    public void setShoppingString(String shoppingString) {
        this.shoppingString = shoppingString;
    }

    public double getMeatValue() {
        return meatValue;
    }

    public void setMeatValue(double meatValue) {
        this.meatValue = meatValue;
    }

    public double getDairyValue() {
        return dairyValue;
    }

    public void setDairyValue(double dairyValue) {
        this.dairyValue = dairyValue;
    }

    public double getCompostValue() {
        return compostValue;
    }

    public void setCompostValue(double compostValue) {
        this.compostValue = compostValue;
    }

    public double getOrganicValue() {
        return organicValue;
    }

    public void setOrganicValue(double organicValue) {
        this.organicValue = organicValue;
    }

    public double getShoppingValue() {
        return shoppingValue;
    }

    public void setShoppingValue(double shoppingValue) {
        this.shoppingValue = shoppingValue;
    }

    public double getFoodValue() {
        return foodValue;
    }

    public void setFoodValue(double foodValue) {
        this.foodValue = foodValue;
    }
}
