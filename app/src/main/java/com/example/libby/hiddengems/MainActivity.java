package com.example.libby.hiddengems;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

import java.io.Serializable;

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
                    final Dialog incompleteWarning = new Dialog(MainActivity.this);
                    incompleteWarning.setContentView(R.layout.dialog);
                    TextView title = (TextView) incompleteWarning.findViewById(R.id.title);
                    title.setVisibility(View.VISIBLE);
                    title.setText("Almost there...");
                    TextView body = (TextView) incompleteWarning.findViewById(R.id.body);
                    body.setVisibility(View.VISIBLE);
                    LinearLayout ll = (LinearLayout) incompleteWarning.findViewById(R.id.dialog_bot);
                    ll.setVisibility(View.VISIBLE);

                    if (!assigned[0]) {
                        Log.e("error", "Start Location is required");
                        title.setText("Where u at?");
                        body.setText("Fill in start location");

                    } else if (!tempPlace[0].isDataValid()) {
                        Log.e("error", "Start Location is not valid");
                        body.setText("Can't find that start location");
                    } else if (!assigned[1]) {
                        Log.e("error", "End Location is required");
                        title.setText("Where u goin?");
                        body.setText("Fill in end location");
                    } else if (!tempPlace[1].isDataValid()) {
                        Log.e("error", "End Location is not valid");
                        body.setText("Can't find that end location");
                    }

                    incompleteWarning.show();
                    Button b = (Button) incompleteWarning.findViewById(R.id.dialogueButton);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {incompleteWarning.dismiss();}
                    });
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
