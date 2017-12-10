package com.example.libby.hiddengems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.util.ArrayList;
import java.lang.reflect.Type;

import static java.lang.Double.max;
import static java.lang.Double.min;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



//    private Button btnRequestDirection;
    private String serverKey = "AIzaSyBold8BEkdg3qQosQk7Fz1uWuyl0GJ9JOk";
    private GoogleMap mMap;

    private Context context;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> as = new ArrayList<>();

//    private Place startPlace;
//    private Place endPlace;
    private int radius;
    private double priceRange;
    private ArrayList<String> userPrefList;
    static MapsActivity maps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.init(this);
        context = getApplicationContext();
        maps = this;

        if(!getIntent().hasExtra("back")) {
            radius = Preferences.getDetourRadius();
            priceRange = Preferences.getBudget();
            userPrefList = Preferences.attractionList;

//        Log.i("Start: ", startPlace.getAddress().toString());
//        Log.i("End: ", endPlace.getAddress().toString());


//        Preferences and budget and radius
//        budget and radius can just be added to the msg

            ArrayList<String> prefs = new ArrayList<>();
            final JSONObject startMsg = new JSONObject();
            try {
                startMsg.put("phone_id", Preferences.getAndroidId());
                startMsg.put("start_place_id", Preferences.getStartId());
                startMsg.put("start_date", Preferences.getStartDate());
                startMsg.put("end_place_id", Preferences.getEndId());
                startMsg.put("end_date", Preferences.getEndDate());
                startMsg.put("budget", priceRange);
                startMsg.put("radius", radius);
                if (userPrefList.isEmpty()) {
                    userPrefList.add("attractions");
                    startMsg.put("keywords", userPrefList);
                } else {
                    startMsg.put("keywords", userPrefList);
                }

                new Utils.sendRoute().execute(startMsg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Utils.sending = true;
            Log.e("back", "has back");
            final Handler handler = new Handler();
            final Runnable task = new Runnable() {
                @Override
                public void run() {
                    Utils.sending = false;
                    drawMap();
                }
            };
            handler.postDelayed(task, 2000);

        }
        Log.i("message", "onCreateStart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button b = findViewById(R.id.maps_negativeButton);
        b.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Preferences.reset();
                 Utils.reset();
                 Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                 startActivity(intent);
             } }
        );

        Button c = findViewById(R.id.maps_preferences);
        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouteActivity.class);
                RouteActivity.goMain = false;
                startActivity(intent);
            }
        });

        Button d = findViewById(R.id.maps_positiveButton);
        d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveInfo save = new SaveInfo();
                Gson gson = new Gson();
                Type type = new TypeToken<SaveInfo>() {}.getType();
                String jj = gson.toJson(save, type);

                new Utils.sendSave(maps, true).execute(jj);
            }
        });

        Button a = findViewById(R.id.maps_save);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    save.put("phone_id", Preferences.getAndroidId());
//                    save.put()
                    SaveInfo save = new SaveInfo();
                    Gson gson = new Gson();
                    Type type = new TypeToken<SaveInfo>() {}.getType();
                    String jj = gson.toJson(save, type);

                    new Utils.sendSave(maps).execute(jj);
                    Log.i("gson", jj);
//                    Log.i("json", save.toString());
                }
                catch (Exception e) {
                    Log.e("json", e.getMessage());
                }
            }
        });

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
//                    Log.i("forecast", info.getForecast());
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
                .execute(new StopInfoDirectionCallback(getApplicationContext()));
    }

    public void reset(){
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }

    public void toastSave() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void driveBaby() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), DriveActivity.class);
                intent.putExtra("firsttime", true);
                startActivity(intent);
            }
        });
    }


    //NOT USED BELOW
    public class StopInfoDirectionCallback implements DirectionCallback {
        Context context;
        public StopInfoDirectionCallback(Context c) {
            context = c;
            if (context == null) {
                Log.e("wtf", "context is null");
            }
        }
        @Override
        public void onDirectionSuccess(Direction direction, String rawBody) {
            if (direction.isOK()) {
                for (StopInfo ll : Utils.arra) {
                    mMap.addMarker(new MarkerOptions().position(ll.getLoc()).title(ll.getName()));
//                    mMap.setOnMarkerClickListener(new StopInfoOnClickListener(context, ll));

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
        }

        @Override
        public void onDirectionFailure(Throwable t) {

        }
    }
    public class StopInfoOnClickListener implements GoogleMap.OnMarkerClickListener {
        StopInfo info;
        Context c;
        public StopInfoOnClickListener(Context cc, StopInfo s) {
            info = s;
            c = cc;
        }

        @Override
        public boolean onMarkerClick(Marker marker) {
            Utils.showDialog(c,
                    true, info.getName(),
                    true, info.getRating(),
                    true, info.getDesc(),
                    false,
                    true, info.getIndex());
            return true;
        }
    }
}
