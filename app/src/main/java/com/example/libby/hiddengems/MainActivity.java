package com.example.libby.hiddengems;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.ui.SupportPlaceAutocompleteFragment;

public class MainActivity extends FragmentActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = (Button) findViewById(R.id.button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                SupportPlaceAutocompleteFragment startFragment = (SupportPlaceAutocompleteFragment)
                        getSupportFragmentManager().findFragmentById(R.id.start_autocomplete_fragment);

                startFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(Place place) {
                        // TODO: Get info about the selected place.
                        Log.i("place selected", "Place: " + place.getName());
                        intent.putExtra("startAddress",place.getName());
//                        Toast.makeText(this, place.getName(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(Status status) {
                        // TODO: Handle the error.
                        Log.i("error", "An error occurred: " + status);
                    }
                });



                startActivity(intent);

            }
        });

    }


}
