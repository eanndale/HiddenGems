package com.example.libby.hiddengems;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Double.max;
import static java.lang.Double.min;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



//    private Button btnRequestDirection;
    private String serverKey = "AIzaSyBold8BEkdg3qQosQk7Fz1uWuyl0GJ9JOk";
    private GoogleMap mMap;

    private ArrayAdapter<String> adapter;
    private ArrayList<String> as = new ArrayList<>();

    private Place startPlace;
    private Place endPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.init(this);
        //TODO EMMIE: Pass all things through intent, Preferences should be a popup with checkboxes on main page
        startPlace = (Place) getIntent().getParcelableExtra("start");
        endPlace = (Place) getIntent().getParcelableExtra("end");
        Log.i("Start: ", startPlace.getAddress().toString());
        Log.i("End: ", endPlace.getAddress().toString());

        /*
        Preferences and budget and radius
        budget and radius can just be added to the msg
         */
        ArrayList<String> prefs = new ArrayList<>();
        JSONObject startMsg = new JSONObject();
        try {
            startMsg.put("start_long", startPlace.getLatLng().longitude);
            startMsg.put("start_lat", startPlace.getLatLng().latitude);
            startMsg.put("end_long", endPlace.getLatLng().longitude);
            startMsg.put("end_lat", endPlace.getLatLng().latitude);

            //TODO EMMIE!!!
            startMsg.put("budget", 0.0);
            startMsg.put("radius", 0.0);
            startMsg.put("Preferences", prefs);

//        Map<String, String> startMsg = new HashMap<>();
//        startMsg.put("start_address", startPlace.getAddress().toString());
//        startMsg.put("end_address", endPlace.getAddress().toString());
//        final AsyncTask<Map<String, Double>, Void, String> msgs = new Utils.sendRoute().execute(startMsg);
            new Utils.sendRoute().execute(startMsg);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        Log.i("message", "onCreateStart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

//        final TextView se = findViewById(R.id.StartToEnd);
//        String s = startPlace.getAddress().toString() + " -> " + endPlace.getAddress().toString();
//        se.setText(s);
        Button b = findViewById(R.id.maps_negativeButton);
        b.setOnClickListener(new View.OnClickListener() {
                                 @Override
                                 public void onClick(View v) {
                                     Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                     startActivity(intent);
                                 }
                             }
        );

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec;
        //Tab 1
        spec = host.newTabSpec("Tab One");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Map");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("Stop List");
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec("Tab Three");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Preferences");
        host.addTab(spec);

//        ArrayAdapter<String> adapter;

//        List<String> values = null; // put values in this
//        values.add("hi");
//
//        adapter = new ArrayAdapter<String>(
//                this,
//                android.R.layout.simple_list_item_multiple_choice,
//                values);
//
//        ListView prefList = (ListView) findViewById(R.id.listView2);
//        prefList.setAdapter(adapter);
//
//        Log.i("message", "onCreateEnd");


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("message", "onMapReady");
        Utils.mapReady = true;
        drawMap();
    }

    public void drawMap() {
        if(Utils.sending || !Utils.mapReady || Utils.arra.isEmpty()) {
            return;
        }

        double max_lat = -300.0, min_lat = 300.0, max_lng = -300.0, min_lng = 300.0;
        for(StopInfo l : Utils.arra) {
            max_lat = max(l.latitude, max_lat);
            min_lat = min(l.latitude, min_lat);
            max_lng = max(l.longitude, max_lng);
            min_lng = min(l.longitude, min_lng);
        }
        double d_lat = max_lat - min_lat;
        double d_lng = max_lng - min_lng;
        LatLngBounds ROUTE = new LatLngBounds(
                new LatLng(min_lat - 0.1*d_lat, min_lng - 0.1*d_lng), new LatLng(max_lat + 0.1*d_lat, max_lng + 0.1*d_lng));
        Log.i("Bounds", ROUTE.toString());

        requestDirection();

        UiSettings settings = mMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        //LatLngBounds ROUTE = new LatLngBounds(start, end);

//
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(ROUTE, 3));
        as = new ArrayList<>();
        for (StopInfo si : Utils.arra) {
            as.add(si.getName());
        }
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, as);
        ListView lw = (ListView) findViewById(R.id.listView);
        lw.setAdapter(adapter);
        lw.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position != 0 || position != Utils.arra.size()-1) {
                    StopInfo info = Utils.arra.get(position);
                    Utils.showDialog(MapsActivity.this,
                            true, info.getName(),
                            true, info.getRating(),
                            true, info.getDesc(),
                            false,
                            true, position);
                }
            }
        });
    }


    public void requestDirection() {
        Log.i("message", "requestDirection");
        Log.i("start", Utils.arra.get(0).toString());
        Log.i("List", Utils.arra.subList(1, Utils.arra.size()-1).toString());
        Log.i("dest", Utils.arra.get(Utils.arra.size()-1).toString());
        ArrayList<LatLng> kun = new ArrayList<LatLng>();
        for (StopInfo s : Utils.arra.subList(1, Utils.arra.size()-1)) {
            kun.add(s.getLoc());
        }

        GoogleDirection.withServerKey(serverKey)
                .from(Utils.arra.get(0).getLoc())
                .to(Utils.arra.get(Utils.arra.size() - 1).getLoc())
                .waypoints(kun)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        if (direction.isOK()) {
                            for(StopInfo ll : Utils.arra) {
                                mMap.addMarker(new MarkerOptions().position(ll.getLoc()).title(ll.getName()));

                                //
//                                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                    @Override
//                                    public boolean onMarkerClick(Marker marker) {
//                                        return false;
//                                    }
//                                });
                            }

                            ArrayList<LatLng> directionPositionList = new ArrayList<>();
                            for (Route r : direction.getRouteList()) {
                                for (Leg leg : r.getLegList()) {
                                    directionPositionList.addAll(leg.getDirectionPoint());
                                }
                            }
                            //direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();

                            mMap.addPolyline(DirectionConverter.createPolyline(getApplicationContext(), directionPositionList, 5, Color.RED));

                        }

                    }

                    @Override
                    public void onDirectionFailure(Throwable t) {
                        // Do something here
                        Log.i("oh shit", "bad");
                    }
                });
    }
    public void reset() {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                Log.i("temp place selected", "tempPlace: " + tempPlace[0].getName());
        intent.putExtra("start", (Parcelable) startPlace);
        intent.putExtra("end", (Parcelable) endPlace);
        startActivity(intent);

    }
}
