package com.example.libby.hiddengems;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    private Place startPlace;
    private Place endPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        //make an array of preferences with its index as its id
        ArrayAdapter<String> adapter;

        List<String> values = new ArrayList<String>(); // put values in this
        values.add("art gallery");
        values.add("bar");
        values.add("campground");

        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                values);

        final ListView prefList = (ListView) findViewById(R.id.prefListView);
        //prefList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        prefList.setAdapter(adapter);

        prefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView)view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                Log.d("jfierojf", "onItemClick: hihi");
            }
        });
        final ArrayList<String> userPrefList = new ArrayList();
        Button go = (Button) findViewById(R.id.search_go_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //SparseBooleanArray checked = prefList.getCheckedItemPositions();
                for (int i = 0; i < prefList.getChildCount(); i++) {
                    if (prefList.isItemChecked(i)) {
                        //String selectedFromList = (prefList.getItemAtPosition(i));
                        String itemValue = (String) prefList.getItemAtPosition(i);
                        Log.d("help", "onClick: " + itemValue);
                        Log.d("help", "message: " + itemValue);
                        userPrefList.add(itemValue);
                    }
                }
                Intent prev = getIntent();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);

                startPlace = (Place) prev.getParcelableExtra("start");
                endPlace = (Place) prev.getParcelableExtra("end");
                boolean [] buttons = prev.getBooleanArrayExtra("priceRange");
                int radius = prev.getIntExtra("radius", 30);

                intent.putExtra("start", (Parcelable)startPlace);
                intent.putExtra("end", (Parcelable)endPlace);
                intent.putExtra("priceRange", buttons);
                intent.putExtra("radius", radius);
                intent.putExtra("preferences", userPrefList);

                startActivity(intent);
            }
        });
    }

}
