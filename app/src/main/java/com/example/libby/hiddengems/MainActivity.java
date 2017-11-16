package com.example.libby.hiddengems;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

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

        final boolean[] iscolor = {true, true, true, true};
        final boolean[] selectedButtons = {false, false, false, false};
        final Button b1 = (Button) findViewById(R.id.button1);
        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(iscolor[0]) {
                    b1.setBackground(getResources().getDrawable(R.drawable.button));
                    iscolor[0] = false;
                    selectedButtons[0] = true;
                }
                else {
                    b1.setBackground(getResources().getDrawable(R.drawable.button_false));
                    iscolor[0] = true;
                    selectedButtons[0] = false;
                }
            }
        });

        final Button b2 = (Button) findViewById(R.id.button2);
        b2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(iscolor[1]) {
                    b2.setBackground(getResources().getDrawable(R.drawable.button));
                    iscolor[1] = false;
                    selectedButtons[1] = true;
                }
                else {
                    b2.setBackground(getResources().getDrawable(R.drawable.button_false));
                    iscolor[1] = true;
                    selectedButtons[1] = false;
                }
            }
        });

        final Button b3 = (Button) findViewById(R.id.button3);
        b3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(iscolor[2]) {
                    b3.setBackground(getResources().getDrawable(R.drawable.button));
                    iscolor[2] = false;
                    selectedButtons[2] = true;
                }
                else {
                    b3.setBackground(getResources().getDrawable(R.drawable.button_false));
                    iscolor[2] = true;
                    selectedButtons[2] = false;
                }
            }
        });

        final Button b4 = (Button) findViewById(R.id.button4);
        b4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                if(iscolor[3]) {
                    b4.setBackground(getResources().getDrawable(R.drawable.button));
                    iscolor[3] = false;
                    selectedButtons[3] = true;
                }
                else {
                    b4.setBackground(getResources().getDrawable(R.drawable.button_false));
                    iscolor[3] = true;
                    selectedButtons[3] = false;
                }
            }
        });

        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar); // initiate the Seek bar
        final TextView textView = (TextView) findViewById(R.id.progress); //progress bar
        textView.setText(seekBar.getProgress() + " miles");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                progress = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText(seekBar.getProgress() + " miles");
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

                Intent intent = new Intent(getApplicationContext(), RouteActivity.class);
//                Log.i("temp place selected", "tempPlace: " + tempPlace[0].getName());
                intent.putExtra("start", (Parcelable) tempPlace[0]);
                intent.putExtra("end", (Parcelable) tempPlace[1]);
                int radius = seekBar.getProgress();
                intent.putExtra("radius", radius);
                intent.putExtra("priceRange", selectedButtons);

                startActivity(intent);

            }
        });

    }


}
