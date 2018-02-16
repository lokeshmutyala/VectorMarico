package com.adjointtechnologies.vectormarico;

/**
 * Created by lokeshmutyala on 31-10-2017.
 */

public class StoreData {
    private String store_name,storeid,landmark,mobile,ownerName,category;
    double latitude,longitude,distance;

    public StoreData(String store_name, double latitude, double longitude, double distance, String storeid, String landmark, String mobile, String ownerName, String category) {
        this.store_name = store_name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance=distance;
        this.storeid=storeid;
        this.landmark=landmark;
        this.mobile=mobile;
        this.ownerName=ownerName;
        this.category=category;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getStoreid() {
        return storeid;
    }

    public void setStoreid(String storeid) {
        this.storeid = storeid;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getStore_name() {
        return store_name;
    }

    public void setStore_name(String store_name) {
        this.store_name = store_name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
