package com.example.libby.hiddengems;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by zaaron on 12/9/2017.
 */

public class NearbyListViewAdapter extends BaseAdapter implements ListAdapter {
    public static final String FIRST_COLUMN = "name";
    public static final String SECOND_COLUMN = "price";
    public static final String THIRD_COLUMN = "distance";
    public ArrayList<JSONObject> list;
    Activity activity;
    TextView txtFirst;
    TextView txtSecond;
    TextView txtThird;
    Button go;


    public NearbyListViewAdapter(Activity activity, ArrayList<JSONObject> list) {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = convertView;
        if (view == null) {

            view = inflater.inflate(R.layout.nearby_row, null);
            ViewHolder holder = new ViewHolder();
            holder.txtFirst = (TextView) view.findViewById(R.id.nearby_name);
            holder.txtSecond = (TextView) view.findViewById(R.id.nearby_price);
            holder.txtThird = (TextView) view.findViewById(R.id.nearby_distance);
            holder.go = (Button) view.findViewById(R.id.nearby_go);
            view.setTag(holder);
        }

        final JSONObject map = list.get(position);
        ViewHolder holder = (ViewHolder)view.getTag();
        try {
            holder.txtFirst.setText(map.getString(FIRST_COLUMN));
            switch (map.getInt(SECOND_COLUMN)) {
                case 1:
                    holder.txtSecond.setText("$");
                    break;
                case 2:
                    holder.txtSecond.setText("$$");
                    break;
                case 3:
                    holder.txtSecond.setText("$$$");
                    break;
                case 4:
                    holder.txtSecond.setText("$$$$");
                    break;
                default:
                    holder.txtSecond.setText("??");
                    break;
            }
            holder.txtThird.setText(map.getString(THIRD_COLUMN));
            holder.go.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        DriveActivity.nearby_lat = map.getDouble("latitude");
                        DriveActivity.nearby_lng = map.getDouble("longitude");
                        DriveActivity.goGoogle = true;
                        activity.finish();
//                        activity.get().routeGoogleTo(map.getDouble("lat"), map.getDouble("lng"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    static class ViewHolder {
        TextView txtFirst;
        TextView txtSecond;
        TextView txtThird;
        Button go;
    }
}