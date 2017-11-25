package com.example.libby.hiddengems;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;

/**
 * Created by zaaron on 11/21/2017.
 */

public class Preferences {

    private static Place startLoc;
    private static Place endLoc;
    private static String startDate;
    private static String endDate;
    private static double budget;
    private static int detourRadius;
    static ArrayList<String> attractionList;
    private static boolean inited = false;

    public static void init() {
        startLoc = null;
        endLoc = null;
        startDate = "";
        endDate = "";
        budget = 500.0;
        detourRadius = 30;
        attractionList = new ArrayList<>();
        attractionList.add("Museum");
        attractionList.add("Natural Landmarks");
    }
    public static boolean isInited() {
        return inited;
    }
    public static void setStartLoc(Place loc) {
        startLoc = loc;
    }
    public static Place getStartLoc() {
        return startLoc;
    }
    public static void setStartDate(String date) {
        startDate = date;
    }
    public static String getStartDate() {
        return startDate;
    }
    public static void setEndDate(String date) {
        endDate = date;
    }
    public static String getEndDate() {
        return endDate;
    }
    public static void setEndLoc(Place loc) {
        endLoc = loc;
    }
    public static Place getEndLoc() {
        return endLoc;
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
