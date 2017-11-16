package com.example.libby.hiddengems;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class RouteActivity extends AppCompatActivity {

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

        ListView prefList = (ListView) findViewById(R.id.prefListView);
        prefList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        prefList.setAdapter(adapter);

        prefList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                CheckedTextView checkedTextView = ((CheckedTextView)view);
                checkedTextView.setChecked(!checkedTextView.isChecked());
            }
        });
        Button go = (Button) findViewById(R.id.search_go_btn);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handle getting which preferences are clicked.
            }
        });
    }

}
