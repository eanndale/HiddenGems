package com.example.libby.hiddengems;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class MainActivity extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Place[] tempPlace = new Place[2];
        final boolean[] assigned = new boolean[2];

        final PlaceAutocompleteFragment startFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);
        startFragment.setHint("Start Location *");
        startFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("place selected", "Place: " + place.getName());
                tempPlace[0] = place;
                assigned[0] = true;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });

        PlaceAutocompleteFragment endFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.end_autocomplete_fragment);
        endFragment.setHint("End Location *");
        endFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i("place selected", "Place: " + place.getName());
                tempPlace[1] = place;
                assigned[1] = true;
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("error", "An error occurred: " + status);
            }
        });



        btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!assigned[0] || !tempPlace[0].isDataValid() || !assigned[1] || !tempPlace[1].isDataValid()) {
                    if (!assigned[0]) {
                        Log.e("error", "Start Location is required");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Fill in start location", true, false, 0);
                    } else if (!tempPlace[0].isDataValid()) {
                        Log.e("error", "Start Location is not valid");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Cannot find that start location", true, false,0);
                    } else if (!assigned[1]) {
                        Log.e("error", "End Location is required");

                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Fill in end location", true, false,0);
                    } else if (!tempPlace[1].isDataValid()) {
                        Log.e("error", "End Location is not valid");
                        Utils.showDialog(MainActivity.this, true, "Almost there...", false,0.0,
                                true, "Cannot find that end location", true, false,0);
                    }
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                Log.i("temp place selected", "tempPlace: " + tempPlace[0].getName());
                intent.putExtra("start", (Parcelable) tempPlace[0]);
                intent.putExtra("end", (Parcelable) tempPlace[1]);
                startActivity(intent);

            }
        });

    }


}
