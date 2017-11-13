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

    public StopInfo() {}
    public StopInfo(String n, String id, double rating, double lat, double lng, double o_lat, double o_lng, int i) {
        this.name = n;
        this.placeid = id;
        this.rating = rating;
        this.latitude = lat;
        this.longitude = lng;
        this.loc = new LatLng(lat, lng);
        this.orig_lat = o_lat;
        this.orig_long = o_lng;
        this.index = i;
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

    public String getDesc() {
        return "";
    }
}
