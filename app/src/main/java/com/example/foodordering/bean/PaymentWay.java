package com.example.foodordering.bean;

/**
 * Created by xch on 2017/3/27.
 */

public class PaymentWay extends RadioBtnSelectedBean{
    private int id;
    private int payImg;
    private String payName;
    private String payHint;

    public int getId() {
        return id;
    }

    public int getPayImg() {
        return payImg;
    }

    public String getPayName() {
        return payName;
    }

    public String getPayHint() {
        return payHint;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setPayImg(int payImg) {
        this.payImg = payImg;
    }

    public void setPayName(String payName) {
        this.payName = payName;
    }

    public void setPayHint(String payHint) {
        this.payHint = payHint;
    }

}
