package com.example.foodordering.bean;

/**
 * Created by xch on 2017/3/29.
 */

public class Business {
    private String Id;//编号
    private String Name;//店名
    private String Contact;//联系方式
    private String Address;//地址
    private String Manager;//店主
    private String OpenTime;//开门时间
    private String ClosedTime;//关门时间
    private double Longitude;    //经度
    private double Latitude;    //纬度

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setContact(String contact) {
        Contact = contact;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setManager(String manager) {
        Manager = manager;
    }

    public void setOpenTime(String openTime) {
        OpenTime = openTime;
    }

    public void setClosedTime(String closedTime) {
        ClosedTime = closedTime;
    }

    public String getId() {
        return Id;
    }

    public String getName() {
        return Name;
    }

    public String getContact() {
        return Contact;
    }

    public String getAddress() {
        return Address;
    }

    public String getManager() {
        return Manager;
    }

    public String getOpenTime() {
        return OpenTime;
    }

    public String getClosedTime() {
        return ClosedTime;
    }

}
