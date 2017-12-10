package com.example.libby.hiddengems;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zaaron on 11/25/2017.
 */

public class DriveActivity extends AppCompatActivity {
    static boolean firstTime = false;
    static boolean done = false;
    static DriveActivity da;
//    static GoogleApiClient locClient;
    static Location mLastLocation;
    static int index = 0;

    static double nearby_lat = 0.0;
    static double nearby_lng = 0.0;
    static boolean goGoogle = false;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            mLastLocation = location;
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };
    private LocationManager mLocationManager;

    private final long LOCATION_REFRESH_TIME = 5*60*1000; //minimum time between in milliseconds
    private final float LOCATION_REFRESH_DISTANCE = 8046.72f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        Preferences.setAndroidId(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        da = this;
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME, LOCATION_REFRESH_DISTANCE, mLocationListener);
            mLastLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        else {
            Log.e("location", "NEVER CREATED");
        }
//        locCallBack lcb = new locCallBack();
//        locClient = new GoogleApiClient.Builder(lcb)
//                .addConnectionCallbacks(lcb)
//                .addOnConnectionFailedListener(lcb)
//                .addApi(LocationServices.API).build();
        firstTime = getIntent().getBooleanExtra("firsttime", false);

        //Get info
        if (firstTime) {
            JSONObject json = new JSONObject();
            try {
                json.put("phone_id", Preferences.getAndroidId());
                new Utils.sendGo(this).execute(json);
            } catch (Exception e) {
                Log.e("from map, drive", e.getStackTrace().toString());
            }
        }
        else if (getIntent().hasExtra("index")){
            int index = getIntent().getIntExtra("index", 0);
            updateCurrent(index);
            DriveActivity.index = index;
        }

        //Rest Stop
        Button stuff_btn = findViewById(R.id.rest_drive);
        stuff_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("reststop", "true");
                    json.put("lat", mLastLocation.getLatitude());
                    json.put("long", mLastLocation.getLongitude());
                } catch (Exception e) {
                    Log.e("reststop", e.getStackTrace().toString());
                }
                new Utils.sendNearby(da).execute(json.toString());
            } }
        );

        //Food
        Button food_btn = findViewById(R.id.food_drive);
        food_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("restaurant", "true");
                    json.put("lat", mLastLocation.getLatitude());
                    json.put("long", mLastLocation.getLongitude());
                } catch (Exception e) {
                    Log.e("food", e.getStackTrace().toString());
                }
                new Utils.sendNearby(da).execute(json.toString());
            } }
        );

        //Gas
        Button gas_btn = findViewById(R.id.gas_drive);
        gas_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("gas_station", "true");
                    json.put("lat", mLastLocation.getLatitude());
                    json.put("long", mLastLocation.getLongitude());
                } catch (Exception e) {
                    Log.e("gas", e.getStackTrace().toString());
                }
                new Utils.sendNearby(da).execute(json.toString());
            } }
        );

        //hotel
        Button hotel_btn = findViewById(R.id.lodging_drive);
        hotel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("budget", Preferences.getBudget());
                    json.put("lodging", "true");
                    json.put("lat", mLastLocation.getLatitude());
                    json.put("long", mLastLocation.getLongitude());
                } catch (Exception e) {
                    Log.e("lodging", e.getStackTrace().toString());
                }
                new Utils.sendNearby(da).execute(json.toString());
            } }
        );

        final Button goArrive = (Button)findViewById(R.id.goArrived);
        goArrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject json = new JSONObject();
                try {
                    json.put("phone_id", Preferences.getAndroidId());
                    if (goArrive.getText().equals("Arrived")) {
                        new Utils.sendArrive().execute(json);
                        if(!done) {
                            goArrive.setText("Go");
                        }
                        else {
                            goArrive.setText("Start Anew?");
                            goArrive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Preferences.reset();
                                    Utils.reset();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    else if(goArrive.getText().equals("Go")) {
                        new Utils.sendGo(da).execute(json);
                        goArrive.setText("Arrived");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if(firstTime) {
            firstTime = false;
            routeGoogleTo(0);
        }
    }

    public void routeGoogleTo(int index) {
        StopInfo si = Utils.arra.get(index);
        StringBuilder s = new StringBuilder();
        s.append("google.navigation:q=");
        s.append(si.latitude);
        s.append(",");
        s.append(si.longitude);
        Uri gmmIntentUri = Uri.parse(s.toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void routeGoogleTo(double lat, double lng) {
        StringBuilder s = new StringBuilder();
        s.append("google.navigation:q=");
        s.append(lat);
        s.append(",");
        s.append(lng);
        Uri gmmIntentUri = Uri.parse(s.toString());
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    public void updateCurrent(int index) {
        DriveActivity.index = index;
        StopInfo curr = Utils.arra.get(index);
        TextView curr_view = (TextView)findViewById(R.id.current_dest);
        curr_view.setText(curr.getName());

        TextView next_view = (TextView)findViewById(R.id.next_dest);
        if(Utils.arra.size() != index+1) {
            StopInfo next = Utils.arra.get(index + 1);
            next_view.setText(next.getName());
            updateWeather(index, true);
        }
        else {
            next_view.setText("Final Destination on route");
            done = true;
            updateWeather(index, false);
        }
    }

    public void updateWeather(int index, boolean hasNext) {
        TextView curr_high = (TextView)findViewById(R.id.current_high);
        TextView curr_low = (TextView)findViewById(R.id.current_low);
        ImageView curr_icon = (ImageView)findViewById(R.id.current_weather);
        JSONObject json = new JSONObject();
        try {
            json.put("lat", Utils.arra.get(index).getLoc().latitude);
            json.put("lng", Utils.arra.get(index).getLoc().longitude);
            new Utils.sendWeather(curr_high, curr_low, curr_icon).execute(json);
        } catch (Exception e) {
            e.printStackTrace();
        }


        TextView next_high = (TextView)findViewById(R.id.next_high);
        TextView next_low = (TextView)findViewById(R.id.next_low);
        ImageView next_icon = (ImageView)findViewById(R.id.next_weather);
        if (!hasNext) {
            next_high.setText("-");
            next_low.setText("-");
            next_icon.setVisibility(View.INVISIBLE);
        }
        else {
            next_icon.setVisibility(View.VISIBLE);
            JSONObject jso = new JSONObject();
            try {
                jso.put("lat", Utils.arra.get(index+1).getLoc().latitude);
                jso.put("lng", Utils.arra.get(index+1).getLoc().longitude);
                new Utils.sendWeather(next_high, next_low, next_icon).execute(jso);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void showNearby(JSONObject json) {
        try {
            NearbyActivity.array = json.getJSONArray("places");
            Intent intent = new Intent(this, NearbyActivity.class);
            startActivity(intent);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (goGoogle) {
            routeGoogleTo(nearby_lat, nearby_lng);
        }
        goGoogle = false;
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
