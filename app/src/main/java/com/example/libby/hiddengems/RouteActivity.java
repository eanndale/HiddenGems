package com.example.libby.hiddengems;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.ArraySet;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RouteActivity extends AppCompatActivity {
    static boolean goMain = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        final Button back = (Button) findViewById(R.id.preferences_back);
        final EditText budgetSpace = (EditText) findViewById(R.id.budget);
        final SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar); // initiate the Seek bar
        final TextView progress = (TextView) findViewById(R.id.progress); //progress bar
        final ListView prefList = (ListView) findViewById(R.id.prefListView);
        final Button add = (Button) findViewById(R.id.Add_Attractions);
        final EditText attractions = (EditText) findViewById(R.id.Attractions);
        final Button clearAttractions = (Button) findViewById(R.id.clear_preferences);
        final Button deleteAttractions = (Button) findViewById(R.id.delete_preferences);

        final ArrayList<String> dels = new ArrayList<>();

        //Back button
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Preferences.setBudget(Double.parseDouble(budgetSpace.getText().toString()));
                Preferences.setDetourRadius(seekBar.getProgress());
                finish();
//                if(goMain) {
//                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                    startActivity(intent);
//                }
//                else {
//                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
//                    intent.putExtra("back", true);
//                    startActivity(intent);
//                }
            }
        });

        //Attraction List
        final ArrayAdapter<String> adapter;
        adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_multiple_choice,
                Preferences.attractionList);


        //Add button
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attractions.getText().toString().equals("Type attractions")) {
                    return;
                }
                Preferences.addAttraction(attractions.getText().toString());
                adapter.notifyDataSetChanged();
                attractions.setText("Type attractions");
            }
        });

        //Clear Attractions
        clearAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.attractionList.clear();
                adapter.notifyDataSetChanged();
            }
        });

        //Delete Selected Attractions
        deleteAttractions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Preferences.attractionList.removeAll(dels);
                adapter.notifyDataSetChanged();
            }
        });

        //Budget
        budgetSpace.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Preferences.setBudget(Double.parseDouble(budgetSpace.getText().toString()));
            }
        });

        //Detour Radius Setup
        seekBar.setProgress(Preferences.getDetourRadius());
        progress.setText(seekBar.getProgress() + " miles");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progress = 0;
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //progress = i;
                progress.setText(seekBar.getProgress() + " miles");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress.setText(seekBar.getProgress() + " miles");
                Preferences.setDetourRadius(seekBar.getProgress());
            }
        });



        //prefList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        prefList.setAdapter(adapter);
        prefList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        prefList.setItemsCanFocus(false);
        prefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView)view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
                if (checkedTextView.isChecked()) {
                    dels.add(Preferences.getAttraction(position));
                    Log.d("click", "onItemClick: add");
                }
                else {
                    dels.remove(Preferences.getAttraction(position));
                    Log.d("vrvrf", "onItemClick: remove");
                }
                Log.d("jfierojf", String.valueOf(position));
            }
        });


    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
