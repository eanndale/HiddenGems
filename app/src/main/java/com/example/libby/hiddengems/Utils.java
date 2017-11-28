package com.example.libby.hiddengems;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by zaaron on 11/11/2017.
 */

public class Utils {
    static boolean sending;
    static boolean mapReady;
    static ArrayList<StopInfo> arra = new ArrayList<>();
    private static MapsActivity ma;

    public static void init(MapsActivity m) {
        ma = m;
    }

    public static JSONObject makeRequest(String path, JSONObject params) throws Exception
    {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(path);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(params.toString());

        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        Log.i("Params", params.toString());

        String str = (String) httpclient.execute(httpost, responseHandler);
        if(str != null)
            return new JSONObject(str);
        return null;
    }

    public static class sendUpdate extends AsyncTask<StopInfo, Void, String> {

        @Override
        protected String doInBackground(StopInfo[] maps) {
            sending = true;
            try {
                Log.e("FIRST", maps[0].getPlaceId());
                JSONObject j = new JSONObject()
                        .put("name", maps[0].getName())
                        .put("place_id", maps[0].getPlaceId())
                        .put("orig_lat", maps[0].getOrig_lat())
                        .put("orig_long", maps[0].getOrig_long())
                        .put("index", maps[0].getIndex());
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/update", j);
                Log.e("SECOND", rsp.getString("place_id"));
                arra.add(rsp.getInt("index"),
                        new StopInfo(
                        rsp.getString("name"),
                        rsp.getString("place_id"),
                        rsp.getDouble("rating"),
                        rsp.getDouble("latitude"),
                        rsp.getDouble("longitude"),
                        rsp.getDouble("orig_lat"),
                        rsp.getDouble("orig_long"),
                        rsp.getInt("index")));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            sending = false;
            mapReady = false;
            ma.reset();
            ma.drawMap();
        }

    }

    public static class sendRoute extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject[] maps) {
            sending = true;
            try {
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/route", maps[0]);
                if (rsp != null) {
                    Log.e("json", rsp.get("places").toString());
                    JSONArray ja = rsp.getJSONArray("places");
                    arra = new ArrayList<>();
//                    arra.add(new StopInfo(
//                            "Start",
//                            null,
//                            0.0,
//                            Double.valueOf(maps[0].get("start_lat").toString()),
//                            Double.valueOf(maps[0].get("start_long").toString()),
//                            0.0,
//                            0.0,
//                            0));
                    for(int i = 0; i < ja.length(); i++) {
                        JSONObject m = ja.getJSONObject(i);
                        arra.add(new StopInfo(
                                m.getString("name"),
                                m.getString("place_id"),
                                m.getDouble("rating"),
                                m.getDouble("latitude"),
                                m.getDouble("longitude"),
                                m.getDouble("orig_lat"),
                                m.getDouble("orig_long"),
                                m.getInt("index")));
                    }
//                    arra.add(new StopInfo(
//                            "End",
//                            null,
//                            0.0,
//                            Double.valueOf(maps[0].get("end_lat").toString()),
//                            Double.valueOf(maps[0].get("end_long").toString()),
//                            0.0,
//                            0.0,
//                            arra.size()));
                }
                else {
                    Log.e("json", " return was null");
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            sending = false;
            ma.drawMap();
        }

    }
//64fffabdc426e18d
    public static void showDialog(Context context,
                                  boolean title_visible, String title,
                                  boolean stars_visible, double starz,
                                  boolean body_visible, String body_text,
                                  boolean dialog_bot_vis,
                                  boolean new_loc_bot_vis, int index) {
//        final AlertDialog.Builder incompleteWarning = new AlertDialog.Builder(context);
        final Dialog incompleteWarning = new Dialog(context);
        incompleteWarning.setContentView(R.layout.dialog);

        TextView titleView = (TextView) incompleteWarning.findViewById(R.id.title);
        if(title_visible) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        }
        else {
            titleView.setVisibility(View.INVISIBLE);
        }

        RatingBar ratingBar = (RatingBar) incompleteWarning.findViewById(R.id.ratingBar);
        if(stars_visible) {
            ratingBar.setRating((float) starz);
            ratingBar.setStepSize(0.1f);
            ratingBar.setVisibility(View.VISIBLE);
        }
        else {
            ratingBar.setVisibility(View.INVISIBLE);
        }

        TextView body = (TextView) incompleteWarning.findViewById(R.id.body);
        if(body_visible) {
            body.setVisibility(View.VISIBLE);
            body.setText(body_text);
        }
        else {
            body.setVisibility(View.INVISIBLE);
        }
        incompleteWarning.show();

        LinearLayout ll;
        if(dialog_bot_vis) {
            ll = (LinearLayout) incompleteWarning.findViewById(R.id.dialog_bot);
            ll.setVisibility(View.VISIBLE);
            Button b = (Button) incompleteWarning.findViewById(R.id.dialogueButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {incompleteWarning.dismiss();}
            });
        }
        else if(new_loc_bot_vis) {
            ll = (LinearLayout) incompleteWarning.findViewById(R.id.new_loc_bot);
            ll.setVisibility(View.VISIBLE);
            Button bad = (Button) incompleteWarning.findViewById(R.id.dialog_negativeButton);
            Button good = (Button) incompleteWarning.findViewById(R.id.dialog_positiveButton);
            final StopInfo si = arra.get(index);
            bad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arra.remove(si);
                    new sendUpdate().execute(si);
                }
            });
            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {incompleteWarning.dismiss();}
            });
        }

    }
}
