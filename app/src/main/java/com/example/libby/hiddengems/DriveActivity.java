package com.example.libby.hiddengems;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

/**
 * Created by zaaron on 11/25/2017.
 */

public class DriveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        Preferences.setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Button stuff_btn = findViewById(R.id.rest_drive);
        stuff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("reststop", "true");
                } catch (Exception e) {
                    Log.e("reststop", e.getStackTrace().toString());
                }
                new Utils.sendNearby().execute(json.toString());
            } }
        );
        Button food_btn = findViewById(R.id.food_drive);
        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("restaurant", "true");
                } catch (Exception e) {
                    Log.e("food", e.getStackTrace().toString());
                }
                new Utils.sendNearby().execute(json.toString());
            } }
        );
        Button gas_btn = findViewById(R.id.gas_drive);
        gas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("gas_station", "true");
                } catch (Exception e) {
                    Log.e("gas", e.getStackTrace().toString());
                }
                new Utils.sendNearby().execute(json.toString());
            } }
        );
        Button hotel_btn = findViewById(R.id.lodging_drive);
        hotel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("lodging", "true");
                } catch (Exception e) {
                    Log.e("lodging", e.getStackTrace().toString());
                }
                new Utils.sendNearby().execute(json.toString());
            } }
        );

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
