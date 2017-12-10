package com.example.libby.hiddengems;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zaaron on 11/11/2017.
 */

public class Utils {
    static boolean sending;
    static boolean mapReady = false;
    static boolean isDriving=false;
    static int betweenLoc=0;
    static ArrayList<StopInfo> arra = new ArrayList<>();
    private static MapsActivity ma;

    public static void init(MapsActivity m) {
        ma = m;
    }

    public static void reset() {
        sending = false;
        mapReady = false;
        arra = new ArrayList<>();
    }

    public static JSONObject makeRequest(String path, String params) throws Exception
    {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the post data
        HttpPost httpost = new HttpPost(path);

        //passes the results to a string builder/entity
        StringEntity se = new StringEntity(params);

        //sets the post request as the resulting string
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        Log.i("Params", params);

        String str = (String) httpclient.execute(httpost, responseHandler);
        if(str != null)
            return new JSONObject(str);
        return null;
    }

    public static JSONObject getRequest(String path, String params) throws Exception
    {
        //instantiates httpclient to make request
        DefaultHttpClient httpclient = new DefaultHttpClient();

        //url with the getURL data
        HttpGet httpost = new HttpGet(path + params);

//        //passes the results to a string builder/entity
//        StringEntity se = new StringEntity(params);
//
//        //sets the post request as the resulting string
//        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        Log.i("Path", path + params);

        String str = (String) httpclient.execute(httpost, responseHandler);
        if(str != null)
            return new JSONObject(str);
        return null;
    }

    public static String jsonToUrl(JSONObject json) {
        StringBuilder ret = new StringBuilder("");
        try {
//            for (Iterator<String> s = json.keys(); s.hasNext(); ) {
//                String key = s.next();
//                String ans = "/" + key + "/" + json.getString(key);
//                ret.append(ans);
////                ret += "/" + json.getString(key);
//            }
            if(json.has("phone_id")) {
                ret.append("/");
                ret.append(json.getString("phone_id"));
            }
            if(json.has("place_id")) {
                ret.append("/");
                ret.append(json.getString("place_id"));
            }
            if(json.has("lat")) {
                ret.append("/");
                ret.append(json.getDouble("lat"));
            }
            if(json.has("lng")) {
                ret.append("/");
                ret.append(json.getDouble("lng"));
            }
        }
        catch (Exception e) {
            Log.e("JsonToURL", e.getStackTrace().toString());
        }
        return ret.toString();
    }

    public static class sendWeather extends AsyncTask<JSONObject, Void, JSONObject> {
        private final WeakReference<TextView> high;
        private final WeakReference<TextView> low;
        private final WeakReference<ImageView> icon;

        public sendWeather(TextView high, TextView low, ImageView icon) {
            this.high = new WeakReference<TextView>(high);
            this.low = new WeakReference<TextView>(low);
            this.icon = new WeakReference<ImageView>(icon);
        }

        @Override
        protected JSONObject doInBackground(JSONObject[] jsonObjects) {
            try {
                JSONObject rsp = Utils.getRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/forecast", jsonToUrl(jsonObjects[0]));
                return rsp;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(result != null) {
                try {
                    if (high.get() != null && result.has("high")) {
                        high.get().setText(String.valueOf(result.getInt("high")));
                    }
                    if (low.get() != null && result.has("low")) {
                        low.get().setText(result.getString("low"));
                    }
//                    JSONObject cast = result.getJSONObject("forecast");
//                    JSONObject daily = cast.getJSONObject("daily");
//                    JSONArray why = daily.getJSONArray("data");
//                    JSONObject killme = why.getJSONObject(0);
//                    if (icon.get() != null && killme.has("icon")) {
                    if (icon.get() != null && result.has("type")) {
                        icon.get().setVisibility(View.VISIBLE);
//                        switch(killme.getString("icon")) {
                        switch(result.getString("type")) {
                            case "fog":
                                icon.get().setImageResource(R.drawable.fog);
                                break;
                            case "clear-day":
                                icon.get().setImageResource(R.drawable.sun);
                                break;
                            case "clear-night":
                                icon.get().setImageResource(R.drawable.night);
                                break;
                            case "rain":
                                icon.get().setImageResource(R.drawable.rain);
                                break;
                            case "snow":
                                icon.get().setImageResource(R.drawable.snow);
                                break;
                            case "cloudy":
                                icon.get().setImageResource(R.drawable.clouds);
                                break;
                            case "sleet":
                                icon.get().setImageResource(R.drawable.sleet);
                                break;
                            case "partly-cloudy-day":
                            case "partly-cloudy-night":
                                icon.get().setImageResource(R.drawable.partly_cloudy);
                                break;
                            case "hail":
                                icon.get().setImageResource(R.drawable.hail);
                                break;
                            case "thunderstorm":
                                icon.get().setImageResource(R.drawable.storm);
                                break;
                            default:
                                icon.get().setVisibility(View.INVISIBLE);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static class sendGo extends AsyncTask<JSONObject, Void, Integer> {
        private final WeakReference<DriveActivity> driveActivity;
        sendGo(DriveActivity d) {
            driveActivity = new WeakReference<DriveActivity>(d);
        }
        @Override
        protected Integer doInBackground(JSONObject[] maps) {
            try {
                JSONObject rsp = Utils.getRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/go", jsonToUrl(maps[0]));
                if (rsp.has("ind")) {
                    return rsp.getInt("ind");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        protected void onPostExecute(Integer res) {
            if (res != -1 && driveActivity.get() != null) {
                driveActivity.get().updateCurrent(res);
                driveActivity.get().routeGoogleTo(res);
            }
        }
    }

    public static class sendArrive extends AsyncTask<JSONObject, Void, Integer> {
        @Override
        protected Integer doInBackground(JSONObject[] maps) {
            try {
                JSONObject rsp = Utils.getRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/arrive", jsonToUrl(maps[0]));
                if (rsp.has("ind")) {
                    return rsp.getInt("ind");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return -1;
        }

    }

    public static class sendAdd extends AsyncTask<JSONObject, Void, Integer> {
//        final WeakReference<ListView> listView;
        final Place place;
        public sendAdd(Place place
//                , ListView listView
        ) {
            this.place = place;
//            this.listView = new WeakReference<ListView>(listView);
        }
        @Override
        protected Integer doInBackground(JSONObject[] maps) {
            JSONArray curr = new JSONArray();
            try {
                for (int i = 0; i < Utils.arra.size(); ++i) {
                    JSONObject json = new JSONObject()
                            .put("lat", arra.get(i).getLoc().latitude)
                            .put("long", arra.get(i).getLoc().longitude);
                    curr.put(json);
                }
                JSONObject j = new JSONObject()
                        .put("lat", place.getLatLng().latitude)
                        .put("long", place.getLatLng().longitude);
                j.put("places", curr);
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/insert", j.toString());
                if (rsp.has("ind")) {
                    return rsp.getInt("ind");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Integer res) {
            LatLng ll = place.getLatLng();
            StopInfo si = new StopInfo(place.getName().toString(), place.getId(), place.getRating(), ll.latitude, ll.longitude, ll.latitude, ll.longitude, res, arra.get(res).getDate());
            arra.add(res, si);
//            Adapter adapter = listView.get().getAdapter();
//            listView.get().setAdapter((ArrayAdapter)adapter);
            ma.drawMap();
        }
    }

    public static class sendSave extends AsyncTask<String, Void, String> {
        private WeakReference<MapsActivity> context;
        private boolean sayGo;
        sendSave(MapsActivity c) {
            this(c, false);
        }
        sendSave(MapsActivity c, boolean b) {
            context = new WeakReference<MapsActivity>(c);
            sayGo = b;
        }
        @Override
        protected String doInBackground(String[] maps) {
            try {
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/route/save", maps[0]);
                if(!sayGo && rsp.has("done") && context.get() != null) {
                    context.get().toastSave();
                }
                else if(sayGo) {
                    return "go";
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
                if (context.get() != null) {
                    context.get().driveBaby();
                }
            }
        }
    }

    public static class sendLoad extends AsyncTask<JSONObject, Void, JSONObject> {
        private final WeakReference<PlaceAutocompleteFragment> start_frag;
        private final WeakReference<PlaceAutocompleteFragment> end_frag;
        private final WeakReference<EditText> start_date;
        private final WeakReference<EditText> end_date;
        private final WeakReference<MainActivity> main;

        public sendLoad(PlaceAutocompleteFragment start_frag, PlaceAutocompleteFragment end_frag,
                        EditText start_date, EditText end_date, MainActivity main) {
            this.start_frag = new WeakReference<PlaceAutocompleteFragment>(start_frag);
            this.end_frag = new WeakReference<PlaceAutocompleteFragment>(end_frag);
            this.start_date = new WeakReference<EditText>(start_date);
            this.end_date = new WeakReference<EditText>(end_date);
            this.main = new WeakReference<MainActivity>(main);
        }

        @Override
        protected JSONObject doInBackground(JSONObject[] maps) {
            try {
                JSONObject rsp = Utils.getRequest( "https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/route/load", jsonToUrl(maps[0]));
                return rsp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if (result != null) {
                Log.i("LOAD RETURN", result.toString());
                try {
                    if (result.has("budget")) {
                        Preferences.setBudget(result.getDouble("budget"));
                    }
                    if (result.has("radius")) {
                        Preferences.setDetourRadius(result.getInt("radius"));
                    }
                    if (result.has("keywords")) {
                        Preferences.attractionList = new ArrayList<>();
                        JSONArray ja = result.getJSONArray("keywords");
                        for (int i = 0; i < ja.length(); ++i) {
                            Preferences.addAttraction(ja.getString(i));
                        }
                    }
                    if (result.has("places")) {
                        arra = new ArrayList<>();
                        JSONArray ja = result.getJSONArray("places");
                        for(int i = 0; i < ja.length(); i++) {
                            JSONObject m = ja.getJSONObject(i);
                            if (i == 0 && start_frag.get() != null) {
                                start_frag.get().setText(m.getString("name"));
                                Preferences.setStartLat(m.getDouble("latitude"));
                                Preferences.setStartLng(m.getDouble("longitude"));
                                Preferences.setStartId(m.getString("place_id"));
                            }
                            else if(i == ja.length()-1 && end_frag.get() != null) {
                                end_frag.get().setText(m.getString("name"));
                                Preferences.setEndLat(m.getDouble("latitude"));
                                Preferences.setEndLng(m.getDouble("longitude"));
                                Preferences.setEndId(m.getString("place_id"));
                            }
                            arra.add(new StopInfo(
                                    m.getString("name"),
                                    m.getString("place_id"),
                                    m.getDouble("rating"),
                                    m.getDouble("latitude"),
                                    m.getDouble("longitude"),
                                    m.getDouble("orig_latitude"),
                                    m.getDouble("orig_longitude"),
                                    arra.size(),
                                    m.getString("date")
//                                ,
//                                m.getString("forecast")
                            ));
                        }
                    }
                    if (result.has("start_date") &&
                            !result.getString("start_date").equals("") &&
                            start_date.get() != null) {
                        String rs = result.getString("start_date");
                        start_date.get().setText(rs);
                        Preferences.setStartDate(rs);
                    }
                    if (result.has("end_date") &&
                            !result.getString("end_date").equals("") &&
                            end_date.get() != null) {
                        String rs = result.getString("end_date");
                        end_date.get().setText(rs);
                        Preferences.setEndDate(rs);
                    }
                    if (result.has("index")) {
                        main.get().loadRoute(result.getInt("index"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static class sendNearby extends AsyncTask<String, Void, JSONObject> {
        final WeakReference<DriveActivity> da;

        public sendNearby(DriveActivity da) {
            this.da = new WeakReference<DriveActivity>(da);
        }


        @Override
        protected JSONObject doInBackground(String[] maps) {
            try {
                JSONObject rsp = Utils.makeRequest( "https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/nearby", maps[0]);
                return rsp;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject res) {
            if (res != null && da.get() != null) {
                da.get().showNearby(res);
            }
        }
    }

    public static class sendUpdate extends AsyncTask<StopInfo, Void, String> {

        @Override
        protected String doInBackground(StopInfo[] maps) {
            sending = true;
            try {
                Log.e("IN UPDATE", "AHHHH");
                Log.e("FIRST", maps[0].getPlaceId());
                JSONObject j = new JSONObject()
                        .put("phone_id", Preferences.getAndroidId())
                        .put("name", maps[0].getName())
                        .put("place_id", maps[0].getPlaceId())
                        .put("orig_lat", maps[0].getOrig_lat())
                        .put("orig_long", maps[0].getOrig_long())
                        .put("index", maps[0].getIndex())
                        .put("radius", Preferences.getDetourRadius())
                        .put("budget", Preferences.getBudget())
                        .put("date", maps[0].getDate());
                ArrayList<String> userPrefList = Preferences.attractionList;
                if (userPrefList.isEmpty()) {
                    userPrefList.add("attractions");
                    j.put("keywords", userPrefList);
                }
                else {
                    j.put("keywords", userPrefList);
                }
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/replace", j.toString());

                Log.e("SECOND", rsp.getString("place_id"));
                arra.add(maps[0].getIndex(),
                        new StopInfo(
                        rsp.getString("name"),
                        rsp.getString("place_id"),
                        rsp.getDouble("rating"),
                        rsp.getDouble("latitude"),
                        rsp.getDouble("longitude"),
                        rsp.getDouble("orig_lat"),
                        rsp.getDouble("orig_long"),
                        maps[0].getIndex(),
                        rsp.getString("date")
//                                ,
//                        rsp.getString("forecast")
                        ));
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
                Log.e("route-Params:", maps[0].toString());
                JSONObject rsp = Utils.makeRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/route", maps[0].toString());
                if (rsp != null) {
                    Log.e("json", rsp.get("places").toString());
                    JSONArray ja = rsp.getJSONArray("places");
                    arra = new ArrayList<>();
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
                                m.getInt("index"),
                                m.getString("date")
//                                ,
//                                m.getString("forecast")
                                ));
                    }
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

    public static class getDesc extends AsyncTask<StopInfo, Void, String> {
        @Override
        protected String doInBackground(StopInfo[] strings) {
            try {
                for(int i = 0; i < strings.length; i++) {
                    JSONObject json = new JSONObject().put("place_id", strings[i].getPlaceId());
                    JSONObject rsp = Utils.getRequest("https://105yog30qc.execute-api.us-east-1.amazonaws.com/api/describe", jsonToUrl(json));
                    StringBuilder sb = new StringBuilder();
                    if (rsp != null && rsp.has("reviews") && rsp.getJSONArray("reviews").length() != 0) {
                        JSONArray ja = rsp.getJSONArray("reviews");
                        for (int j = 0; j < ja.length() && j < 3; j++) {
                            JSONObject rev = ja.getJSONObject(j);
                            if (rev.has("author_name")) {
                                sb.append(rev.getString("author_name"));
                            }
                            else {
                                sb.append("Anon");
                            }
                            sb.append("\n\t");
                            sb.append(rev.getString("text"));
                            sb.append("\n");
                        }
                    }
                    if (sb.length() > 0) {
                        Utils.arra.get(Utils.arra.indexOf(strings[i])).setDesc(sb.toString());
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
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
            if (starz != 0) {
                ratingBar.setRating((float) starz);
                ratingBar.setStepSize(0.1f);
                ratingBar.setIsIndicator(true);
                ratingBar.setVisibility(View.VISIBLE);
            }
            else {
                ratingBar.setVisibility(View.INVISIBLE);
            }
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

        LinearLayout ll = (LinearLayout) incompleteWarning.findViewById(R.id.dialog_bot);
        if(dialog_bot_vis) {
            ll.setVisibility(View.VISIBLE);
            Button b = (Button) incompleteWarning.findViewById(R.id.dialogueButton);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {incompleteWarning.dismiss();}
            });
        }
        else if(new_loc_bot_vis) {
            ll.setVisibility(View.INVISIBLE);
            ll = (LinearLayout) incompleteWarning.findViewById(R.id.new_loc_bot);
            ll.setVisibility(View.VISIBLE);
            Button bad = (Button) incompleteWarning.findViewById(R.id.dialog_negativeButton);
            Button remove = (Button) incompleteWarning.findViewById(R.id.dialog_removeButton);
            Button good = (Button) incompleteWarning.findViewById(R.id.dialog_positiveButton);
            final StopInfo si = arra.get(index);
            bad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arra.remove(si);
                    new sendUpdate().execute(si);
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    arra.remove(si);
                    ma.drawMap();
                    incompleteWarning.dismiss();
                }
            });
            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {incompleteWarning.dismiss();}
            });
        }

    }
}
