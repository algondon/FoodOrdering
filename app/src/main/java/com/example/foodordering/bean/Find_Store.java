package com.example.foodordering.bean;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by xch on 2018/3/20
 */
//将类序列化之后可以在Activity之间传输数据
public class Find_Store implements Serializable {
    private int id;
    private String name;
    private int image;//图片
    private double price;//价格
    private int rating;//星级
    private double eva_per_month;//分数
    private double delivery_price;//配送费
    private double distances;//距离
    private String MonthSellCount;//月销售
    private double man_totals;//满多少
    private double man_salls;//减多少
    private double di;//抵
    private boolean zhuan;//专
    private boolean su;//速
    private boolean piao;//票

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public double getEva_per_month() {
        return eva_per_month;
    }

    public void setEva_per_month(double eva_per_month) {
        this.eva_per_month = eva_per_month;
    }

    public double getDelivery_price() {
        return delivery_price;
    }

    public void setDelivery_price(double delivery_price) {
        this.delivery_price = delivery_price;
    }

    public double getDistances() {
        return distances;
    }

    public void setDistances(double distances) {
        this.distances = distances;
    }

    public String getMonthSellCount() {
        return MonthSellCount;
    }

    public void setMonthSellCount(String monthSellCount) {
        MonthSellCount = monthSellCount;
    }

    public double getMan_totals() {
        return man_totals;
    }

    public void setMan_totals(double man_totals) {
        this.man_totals = man_totals;
    }

    public double getMan_salls() {
        return man_salls;
    }

    public void setMan_salls(double man_salls) {
        this.man_salls = man_salls;
    }

    public double getDi() {
        return di;
    }

    public void setDi(double di) {
        this.di = di;
    }

    public boolean isZhuan() {
        return zhuan;
    }

    public void setZhuan(boolean zhuan) {
        this.zhuan = zhuan;
    }

    public boolean isSu() {
        return su;
    }

    public void setSu(boolean su) {
        this.su = su;
    }

    public boolean isPiao() {
        return piao;
    }

    public void setPiao(boolean piao) {
        this.piao = piao;
    }
}
