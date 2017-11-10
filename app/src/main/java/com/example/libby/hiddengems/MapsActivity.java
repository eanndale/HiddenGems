package com.example.libby.hiddengems;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.PolylineOptions;


import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;
import static java.lang.Double.min;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



//    private Button btnRequestDirection;
    private String serverKey = "AIzaSyBold8BEkdg3qQosQk7Fz1uWuyl0GJ9JOk";
    private GoogleMap mMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.i("message", "onCreateStart");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        TabHost host = (TabHost)findViewById(R.id.tabHost);
        host.setup();

        TabHost.TabSpec spec = host.newTabSpec("Tab One");
        //Tab 1

        spec.setContent(R.id.tab1);
        spec.setIndicator("Map");
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec("Tab Two");
        spec.setContent(R.id.tab2);
        spec.setIndicator("List");
        host.addTab(spec);

        Log.i("message", "onCreateEnd");


    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.i("message", "onMapReady");

        Place startPlace = (Place) getIntent().getParcelableExtra("start");
        Place endPlace = (Place) getIntent().getParcelableExtra("end");

        LatLng start = startPlace.getLatLng();
        LatLng end = endPlace.getLatLng();
        double max_lat = max(start.latitude, end.latitude);
        double min_lat = min(start.latitude, end.latitude);
        double max_lng = max(start.longitude, end.longitude);
        double min_lng = min(start.longitude, end.longitude);

        LatLngBounds ROUTE = new LatLngBounds(
                new LatLng(min_lat - 0.01, min_lng - 0.01), new LatLng(max_lat + 0.01, max_lng + 0.01));


        requestDirection(start, end);

        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

        //LatLngBounds ROUTE = new LatLngBounds(start, end);

//
//        int width = getResources().getDisplayMetrics().widthPixels;
//        int height = getResources().getDisplayMetrics().heightPixels;
//        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(ROUTE, 3));

    }

    public void requestDirection(final LatLng start, final LatLng end) {
        Log.i("message", "requestDirection");

        GoogleDirection.withServerKey(serverKey)
                .from(start)
                .to(end)
                .transportMode(TransportMode.DRIVING)
                .execute(new DirectionCallback() {
                    @Override
                    public void onDirectionSuccess(Direction direction, String rawBody) {
                        // Do something here
                        if (direction.isOK()) {
                            mMap.addMarker(new MarkerOptions().position(start));
                            mMap.addMarker(new MarkerOptions().position(end));

                            ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
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

}
