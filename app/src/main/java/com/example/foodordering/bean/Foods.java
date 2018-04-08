package com.example.foodordering.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by xch on 2017/3/1.
 */
//将类序列化之后可以在Activity之间传输数据
public class Foods extends DataSupport implements Serializable{
    private int id;
    private String name;
    private String imageUrl;//图片
    private double price;//价格
    private String ingredients;//配料
    private String description;//简介
    private int rating;//星级
    public int count;//购买数量

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getPrice() {
        return price;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getDescription() {
        return description;
    }

    public int getRating() {
        return rating;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

}
