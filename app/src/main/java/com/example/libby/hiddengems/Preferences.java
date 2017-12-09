package com.example.libby.hiddengems;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by zaaron on 11/21/2017.
 */

public class Preferences {
    private static String androidId;
    private static Place startLoc;
    private static Place endLoc;
    private static String startId;
    private static String endId;
    private static double startLat;
    private static double startLng;
    private static double endLat;
    private static double endLng;
    private static String startDate;
    private static String endDate;
    private static double budget;
    private static int detourRadius;
    static ArrayList<String> attractionList;
    private static boolean inited = false;

    public static void init() {
        androidId = "";
        startLoc = null;
        endLoc = null;
        startDate = "";
        endDate = "";
        startId = "";
        endId = "";
        startLat = 0.0;
        startLng = 0.0;
        endLat = 0.0;
        endLng = 0.0;
        budget = 500.0;
        detourRadius = 30;
        attractionList = new ArrayList<>();
        attractionList.add("Museum");
        attractionList.add("Natural Landmarks");
        inited = true;
    }
    public static void reset() {
        startLoc = null;
        endLoc = null;
        startDate = "";
        endDate = "";
        startId = "";
        endId = "";
        startLat = 0.0;
        startLng = 0.0;
        endLat = 0.0;
        endLng = 0.0;
    }
    public static boolean isInited() {
        return inited;
    }
    public static void setAndroidId(String s) {
        androidId = s;
    }
    public static String getAndroidId() {
        return androidId;
    }
    public static void setStartLoc(Place loc) {
        startLoc = loc;
        startId = loc.getId();
        startLat = loc.getLatLng().latitude;
        startLng = loc.getLatLng().longitude;
    }
    public static void setStartId(String id) { startId = id; }
    public static String getStartId() { return startId; }
    public static void setStartLat(double lat) { startLat = lat; }
    public static double getStartLat() { return startLat; }
    public static void setStartLng(double lng) { startLng = lng; }
    public static double getStartLng() { return startLng; }
    public static Place getStartLoc() { return startLoc; }
    public static void setStartDate(String date) {
        startDate = date;
    }
    public static String getStartDate() { return startDate; }

    public static void setEndLoc(Place loc) {
        endLoc = loc;
        endId = loc.getId();
        endLat = loc.getLatLng().latitude;
        endLng = loc.getLatLng().longitude;
    }
    public static void setEndId(String id) { endId = id; }
    public static String getEndId() { return endId; }
    public static void setEndLat(double lat) { endLat = lat; }
    public static double getEndLat() { return endLat; }
    public static void setEndLng(double lng) { endLng = lng; }
    public static double getEndLng() { return endLng; }
    public static Place getEndLoc() { return endLoc; }
    public static void setEndDate(String date) {
        endDate = date;
    }
    public static String getEndDate() {
        return endDate;
    }

    public static void setBudget(double b) {
        budget = b;
    }
    public static double getBudget() {
        return budget;
    }
    public static void setDetourRadius(int d) {
        detourRadius = d;
    }
    public static int getDetourRadius() {
        return detourRadius;
    }
    public static void addAttraction(String a) {
        attractionList.add(a);
    }
    public static void removeAttraction(String a) {
        attractionList.remove(a);
    }
    public static String getAttraction(int i) {
        return attractionList.get(i);
    }
}
