package com.example.libby.hiddengems;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

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

import java.io.Serializable;

//public class MyBottomDialogFragment extends BottomSheetDialogFragment {
//
//    @SuppressLint("RestrictedApi")
//    @Override
//    public void setupDialog(final Dialog dialog, int style) {
//        super.setupDialog(dialog, style);
//        View contentView = View.inflate(getContext(), R.layout.fragment_bottomsheet3, null);
//        dialog.setContentView(contentView);
//    }
//}

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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



        Place startPlace = (Place) getIntent().getParcelableExtra("start");
        Place endPlace = (Place) getIntent().getParcelableExtra("end");

        LatLng start = startPlace.getLatLng();
        LatLng end = endPlace.getLatLng();

        LatLngBounds ROUTE = new LatLngBounds(start, end);


        Marker startMarker = mMap.addMarker(new MarkerOptions().position(start).title((String) startPlace.getAddress()));
        Marker endMarker = mMap.addMarker(new MarkerOptions().position(end).title((String) endPlace.getAddress()));
        startMarker.showInfoWindow();
        endMarker.showInfoWindow();

        UiSettings settings = googleMap.getUiSettings();
        settings.setZoomControlsEnabled(true);

//        BottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomDialogFragment ();
//        bottomSheetDialogFragment.show(getSupportFragmentManager(),bottomSheetDialogFragment.getTag());

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ROUTE.getCenter(), 15));






    }
}
