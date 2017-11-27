package com.example.libby.hiddengems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by zaaron on 11/25/2017.
 */

public class DriveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        Preferences.setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        Button stuff_btn = findViewById(R.id.attractions_drive);
        stuff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            } }
        );
        Button food_btn = findViewById(R.id.food_drive);
        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            } }
        );
        Button gas_btn = findViewById(R.id.gas_drive);
        gas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            } }
        );
        Button hotel_btn = findViewById(R.id.lodging_drive);
        hotel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            } }
        );
        Button search_btn = findViewById(R.id.search_drive);
        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
