package com.usc.divyajagadeesh.travelandentertainment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";

    // vars
    private ArrayList<String> mImgUrls = new ArrayList<>();
    private ArrayList<String> mPlaceNames = new ArrayList<>();
    private ArrayList<String> mPlaceLocations = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Log.d(TAG, "onCreate: started.");

        JSONObject json = getIncomingIntent();
        initImgBitmaps(json);
    }

    private JSONObject  getIncomingIntent() {
        Log.d(TAG, "getIncomingIntent: checking for incoming intents.");
        JSONObject jsonObject = null;
        if (getIntent().hasExtra("json")){
            String json = getIntent().getStringExtra("json");
            // turn string into json object
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonObject;
    }

    private void initImgBitmaps(JSONObject jsonObject){
        Log.d(TAG, "initImgBitmaps: preparing bitmaps.");

        // print json object
        try {
            Log.d(TAG, jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get results array
        JSONArray results = null;
        int length = 0;
        try {
            results = jsonObject.getJSONArray("results");
            length = results.length();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < length; i++){
            try {
                String curr_name = results.getJSONObject(i).getString("name");
                String curr_img = results.getJSONObject(i).getString("icon");
                String curr_loc = results.getJSONObject(i).getString("vicinity");
                mImgUrls.add(curr_img);
                mPlaceNames.add(curr_name);
                mPlaceLocations.add(curr_loc);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mImgUrls, mPlaceNames, mPlaceLocations);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
