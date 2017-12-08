package com.example.libby.hiddengems;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btn;
    private SimpleDateFormat dateFormatter;
    private EditText startDate;
    private EditText endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dateFormatter = new SimpleDateFormat("mm-dd-yyyy", Locale.US);

        if(!Preferences.isInited()) {
            Preferences.init();
            Preferences.setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        }

        //final Place[] tempPlace = new Place[2];
        final boolean[] assigned = new boolean[2];

        final PlaceAutocompleteFragment startFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);
        startFragment.setHint("Start Location *");
        if (Preferences.getStartLoc() != null) {
            assigned[0] = true;
            startFragment.setText(Preferences.getStartLoc().getName());
        }
        startFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("place selected", "Place: " + place.getName());
                //tempPlace[0] = place;
                assigned[0] = true;
                Preferences.setStartLoc(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        startDate = (EditText) findViewById(R.id.start_date);
        startDate.setText("Start Date - MMDDYYYY");
        if (!Preferences.getStartDate().equals("")) {
            startDate.setText(Preferences.getStartDate());
        }

        PlaceAutocompleteFragment endFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.end_autocomplete_fragment);
        endFragment.setHint("End Location *");
        if (Preferences.getEndLoc() != null) {
            assigned[1] = true;
            endFragment.setText(Preferences.getEndLoc().getName());
        }
        endFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("place selected", "Place: " + place.getName());
                //tempPlace[1] = place;
                assigned[1] = true;
                Preferences.setEndLoc(place);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        endDate = (EditText) findViewById(R.id.end_date);
        endDate.setText("End Date - MMDDYYYY");
        if (!Preferences.getEndDate().equals("")) {
            endDate.setText(Preferences.getEndDate());
        }

        btn = (Button) findViewById(R.id.start_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.setStartDate(startDate.getText().toString());
                Preferences.setEndDate(endDate.getText().toString());
                if(!assigned[0] || !Preferences.getStartLoc().isDataValid() || !checkDate(startDate.getText().toString())
                        || !assigned[1] || !Preferences.getEndLoc().isDataValid() || !checkDate(endDate.getText().toString()) ) {
                    if (!assigned[0]) {
                        Log.e("error", "Start Location is required");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Fill in start location", true, false, 0);
                    } else if (!Preferences.getStartLoc().isDataValid()) {
                        Log.e("error", "Start Location is not valid");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Cannot find that start location", true, false,0);
                    } else if (!assigned[1]) {
                        Log.e("error", "End Location is required");

                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Fill in end location", true, false,0);
                    } else if (!Preferences.getEndLoc().isDataValid()) {
                        Log.e("error", "End Location is not valid");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Cannot find that end location", true, false,0);
                    } else if (!checkDate(startDate.getText().toString())) {
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false, 0.0,
                                true, "Start date must be formatted like MMDDYYYY", true, false, 0);
                    } else if (!checkDate(endDate.getText().toString())) {
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false, 0.0,
                                true, "End date must be formatted like MMDDYYYY", true, false, 0);
                    }
                    return;
                }
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                Log.i("temp place selected", "tempPlace: " + tempPlace[0].getName());
//                intent.putExtra("start", (Parcelable) tempPlace[0]);
//                intent.putExtra("end", (Parcelable) tempPlace[1]);

                startActivity(intent);
            }
        });

        Button butt = (Button) findViewById(R.id.preference_main);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean checkDate(String s) {
        if (s.equals("Start Date - MMDDYYYY")) {
            Preferences.setStartDate("");
            return true;
        }
        else if(s.equals("End Date - MMDDYYYY")) {
            Preferences.setEndDate("");
            return true;
        }
        int month = Integer.decode(s.substring(0, 2));
        int day = Integer.decode(s.substring(2,4));
        int year = Integer.decode(s.substring(4));
        return (month > 0 && month < 13) && (day > 0 && day < 32) && (year > 1900 && year < 2100);
    }

    private boolean checkPermissions() {
        //NOTE ADD PERMISSIONS if necessary
        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
        permissions.add(Manifest.permission.INTERNET);

        ArrayList<String> reqs = new ArrayList<>();
        for (int i = 0; i < permissions.size(); i++) {
            int res = ContextCompat.checkSelfPermission(MainActivity.this, permissions.get(i));
            if (res != PackageManager.PERMISSION_GRANTED) {
                reqs.add(permissions.get(i));
            }
        }
        if (!reqs.isEmpty()) {
            ActivityCompat.requestPermissions(this, reqs.toArray(new String[reqs.size()]), 1);
            return false;
        }
        return true;
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
                if (v.getId() == R.id.start_date && checkDate(((EditText) v).getText().toString())) {
                    Preferences.setStartDate(((EditText) v).getText().toString());
                }
                else if(v.getId() == R.id.end_date && checkDate(((EditText) v).getText().toString())) {
                    Preferences.setEndDate(((EditText) v).getText().toString());
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
