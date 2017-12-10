package com.example.libby.hiddengems;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by zaaron on 11/12/2017.
 */

public class StopInfo {
    double latitude;
    double longitude;

    private String name;
    private String placeid;
    private double rating;
    private LatLng loc;
    private double orig_lat;
    private double orig_long;
    private int index;
    private String date;
    private String desc = "Yikes! There are no reviews for this location.";
    private String forecast;

    public StopInfo() {}
    public StopInfo(String n, String id, double rating, double lat, double lng, double o_lat, double o_lng, int i, String date
//            , String forecast
    ) {
        this.name = n;
        this.placeid = id;
        this.rating = rating;
        this.latitude = lat;
        this.longitude = lng;
        this.loc = new LatLng(lat, lng);
        this.orig_lat = o_lat;
        this.orig_long = o_lng;
        this.index = i;
        this.date = date;
//        this.forecast = forecast;
    }

    public LatLng getLoc() {
        return loc;
    }

    public String getName() {
        return name;
    }

    public String getPlaceId() {
        return placeid;
    }

    public double getRating() {
        return rating;
    }

    public double getOrig_lat() {
        return orig_lat;
    }

    public double getOrig_long() {
        return orig_long;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int i) {this.index = i;}

    public String getDesc() { return desc; }

    public void setDesc(String d) { desc = d; }

    public String getDate() {
        return date;
    }

//    public String getForecast() { return forecast; }
}
