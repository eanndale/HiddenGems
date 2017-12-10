package com.example.libby.hiddengems;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by zaaron on 12/9/2017.
 */

public class NearbyActivity extends AppCompatActivity {
    static JSONArray array = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby);

        try {
            ArrayList<JSONObject> arrayList = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                arrayList.add(array.getJSONObject(i));
            }
            ListView listView = (ListView) findViewById(R.id.nearby_list);
            listView.setAdapter(new NearbyListViewAdapter(this, arrayList));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Button nearby_back = findViewById(R.id.nearby_back);
        nearby_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
