package com.example.foodordering.bean;

/**
 * Created by xch on 2018/3/9.
 */

public class SetMeal {
    private String StapleFood;
    private String MeatDishes;
    private String VegetarianDishes1;
    private String VegetarianDishes2;
    private String Energy;//能量值
    private String Date;
    private String ImageUrl;
    private int Rating;
    private String MonthSellCount;//月销售
    private String Price;

    public int getRating() {
        return Rating;
    }

    public void setRating(int rating) {
        Rating = rating;
    }
    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getMonthSellCount() {
        return MonthSellCount;
    }

    public void setMonthSellCount(String monthSellCount) {
        MonthSellCount = monthSellCount;
    }

    public String getStapleFood() {
        return StapleFood;
    }

    public void setStapleFood(String stapleFood) {
        StapleFood = stapleFood;
    }

    public String getMeatDishes() {
        return MeatDishes;
    }

    public void setMeatDishes(String meatDishes) {
        MeatDishes = meatDishes;
    }

    public String getVegetarianDishes1() {
        return VegetarianDishes1;
    }

    public void setVegetarianDishes1(String vegetarianDishes1) {
        VegetarianDishes1 = vegetarianDishes1;
    }

    public String getVegetarianDishes2() {
        return VegetarianDishes2;
    }

    public void setVegetarianDishes2(String vegetarianDishes2) {
        VegetarianDishes2 = vegetarianDishes2;
    }

    public String getEnergy() {
        return Energy;
    }

    public void setEnergy(String energy) {
        Energy = energy;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
