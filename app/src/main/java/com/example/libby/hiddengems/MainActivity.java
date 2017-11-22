package com.example.libby.hiddengems;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
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
import java.util.Calendar;
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

        dateFormatter = new SimpleDateFormat("dd-mm-yyyy", Locale.US);

        if(!Preferences.isInited()) {
            Preferences.init();
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
//        startDate.setInputType(InputType.TYPE_NULL);
//        final Calendar newCal = Calendar.getInstance();
//        startDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startDate.requestFocus();
//                DatePickerDialog startDateDialogue = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, month, dayOfMonth);
//                        startDate.setText(dateFormatter.format(newDate.getTime()));
//                    }
//                }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));
//                startDateDialogue.show();
//            }
//        });

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
//        endDate.setInputType(InputType.TYPE_NULL);
//        endDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DatePickerDialog startDateDialogue = new DatePickerDialog(getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        Calendar newDate = Calendar.getInstance();
//                        newDate.set(year, month, dayOfMonth);
//                        endDate.setText(dateFormatter.format(newDate.getTime()));
//                    }
//                }, newCal.get(Calendar.YEAR), newCal.get(Calendar.MONTH), newCal.get(Calendar.DAY_OF_MONTH));
//            }
//        });

        btn = (Button) findViewById(R.id.start_main);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!assigned[0] || !Preferences.getStartLoc().isDataValid() || !assigned[1] || !Preferences.getEndLoc().isDataValid()) {
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


}
