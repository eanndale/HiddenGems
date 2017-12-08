package com.example.libby.hiddengems;

import java.util.ArrayList;

/**
 * Created by zaaron on 12/3/2017.
 */


public class SaveInfo {
    String phone_id;
    String start_place_id;
    String start_date;
    String end_place_id;
    String end_date;
    double budget;
    int radius;
    ArrayList<String> keywords;
    ArrayList<StopInfo> places;

    public SaveInfo() {
        this(Preferences.getAndroidId(),
                Preferences.getStartLoc().getId(), Preferences.getStartDate(),
                Preferences.getEndLoc().getId(), Preferences.getEndDate(),
                Preferences.getBudget(), Preferences.getDetourRadius(),
                Preferences.attractionList, Utils.arra);
    }


    public SaveInfo(String pid, String sid, String sdate, String eid, String edate, double b, int r, ArrayList<String> key, ArrayList<StopInfo> stops) {
        phone_id = pid;
        start_place_id = sid;
        start_date = sdate;
        end_place_id = eid;
        end_date = edate;
        budget = b;
        radius = r;
        keywords = key;
        places = stops;
    }



    public String getPhone_id() {
        return this.phone_id;
    }

    public void setPhone_id(String value) {
        this.phone_id = value;
    }

    public String getStart_place_id() {
        return this.start_place_id;
    }

    public void setStart_place_id(String value) {
        this.start_place_id = value;
    }

    public String getStart_date() {
        return this.start_date;
    }

    public void setStart_date(String value) {
        this.start_date = value;
    }

    public String getEnd_place_id() {
        return this.end_place_id;
    }

    public void setEnd_place_id(String value) {
        this.end_place_id = value;
    }

    public String getEnd_date() {
        return this.end_date;
    }

    public void setEnd_date(String value) {
        this.end_date = value;
    }

    public double getBudget() {
        return this.budget;
    }

    public void setBudget(double value) {
        this.budget = value;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int value) {
        this.radius = value;
    }

    public ArrayList<String> getKeywords() {
        return this.keywords;
    }

    public void setKeywords(ArrayList<String> value) {
        this.keywords = value;
    }

    public ArrayList<StopInfo> getPlaces() {
        return this.places;
    }

    public void setPlaces(ArrayList<StopInfo> value) {
        this.places = value;
    }
}
