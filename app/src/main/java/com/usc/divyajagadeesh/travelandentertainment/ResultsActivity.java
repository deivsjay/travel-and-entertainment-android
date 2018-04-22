package com.usc.divyajagadeesh.travelandentertainment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    private static final String TAG = "ResultsActivity";

    // vars
    private ArrayList<String> mImgUrls1 = new ArrayList<>();
    private ArrayList<String> mPlaceNames1 = new ArrayList<>();
    private ArrayList<String> mPlaceLocations1 = new ArrayList<>();
    private ArrayList<String> mImgUrls2 = new ArrayList<>();
    private ArrayList<String> mPlaceNames2 = new ArrayList<>();
    private ArrayList<String> mPlaceLocations2 = new ArrayList<>();
    private ArrayList<String> mImgUrls3 = new ArrayList<>();
    private ArrayList<String> mPlaceNames3 = new ArrayList<>();
    private ArrayList<String> mPlaceLocations3 = new ArrayList<>();
    private int pageNum = 1;
    private String keyword;
    private String type;
    private String radius;
    private String lat;
    private String lon;
    RequestQueue queue = MySingleton.getInstance(this).getRequestQueue();
    private JSONObject jsonObject;
    ProgressDialog progressDialog;

    // buttons
    Button next;
    Button previous;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Log.d(TAG, "onCreate: started.");

        // set progress dialog for future use
        progressDialog = new ProgressDialog(ResultsActivity.this);
        progressDialog.setMessage("Fetching results");

        jsonObject = getIncomingIntent();
        initImgBitmaps(jsonObject);

        // next button clicked
        final Button nextButton = findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: next button clicked.");
                pageNum = pageNum + 1;
                if ((pageNum == 2 && mPlaceNames2.size() != 0) || (pageNum == 3 && mPlaceNames3.size() != 0)){
                    initRecyclerView();
                    // previous button clickable
                    previous = findViewById(R.id.previous);
                    previous.setClickable(true);
                    previous.setTextColor(Color.BLACK);
                    // next button: set clickable/unclickable
                    next = findViewById(R.id.next);
                    if (pageNum == 2 && mPlaceNames3.size() != 0){
                        next.setClickable(true);
                        next.setTextColor(Color.BLACK);
                    }
                    else if (pageNum == 2 && mPlaceNames3.size() == 0){
                        next.setClickable(false);
                        next.setTextColor(Color.GRAY);
                    }
                    else if (pageNum == 3){
                        next.setClickable(false);
                        next.setTextColor(Color.GRAY);
                    }
                }
                else {
                    progressDialog.show();
                    getNextPlaces();
                }
            }
        });

        // previous button clicked
        final Button previousButton = findViewById(R.id.previous);
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: previous button clicked.");
                pageNum = pageNum - 1;
                initRecyclerView();
                // make next clickable
                next = findViewById(R.id.next);
                next.setClickable(true);
                next.setTextColor(Color.BLACK);
                // make previous unclickable
                previous = findViewById(R.id.previous);
                if (pageNum == 1){
                    previous.setClickable(false);
                    previous.setTextColor(Color.GRAY);
                }
                else {
                    previous.setClickable(true);
                    previous.setTextColor(Color.BLACK);
                }

            }
        });
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

        // get keyword
        if (getIntent().hasExtra("keyword")){
            keyword = getIntent().getStringExtra("keyword");
            Log.d(TAG, "getIncomingIntent (keyword): " + keyword);
        }

        // get type
        if (getIntent().hasExtra("type")){
            type = getIntent().getStringExtra("type");
            Log.d(TAG, "getIncomingIntent (type): " + type);
        }

        // get radius
        if (getIntent().hasExtra("radius")){
            radius = getIntent().getStringExtra("radius");
            Log.d(TAG, "getIncomingIntent (radius): " + radius);
        }

        // get latitude
        if (getIntent().hasExtra("lat")){
            lat = getIntent().getStringExtra("lat");
            Log.d(TAG, "getIncomingIntent (lat): " + lat);
        }

        // get longitude
        if (getIntent().hasExtra("lon")){
            lon = getIntent().getStringExtra("lon");
            Log.d(TAG, "getIncomingIntent (lon): " + lat);
        }

        return jsonObject;
    }

    private void getNextPlaces(){

        // get next page token
        next = findViewById(R.id.next);
        String nextPageToken = null;
        if (jsonObject.has("next_page_token")){
            try {
                nextPageToken = jsonObject.getString("next_page_token");
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        // get variable fields
        String url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com" +
                "/morePlaces" +
                "?keyword=" + keyword +
                "&type=" + type +
                "&radius=" + radius +
                "&lat=" + lat +
                "&lon=" + lon;
        if (nextPageToken != null){
            url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com" +
                    "/morePlaces" +
                    "?keyword=" + keyword +
                    "&type=" + type +
                    "&radius=" + radius +
                    "&lat=" + lat +
                    "&lon=" + lon +
                    "&pagetoken=" + nextPageToken;
        }

        Log.d(TAG, "getNextPlaces: " + url);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "onResponse: got json for more places.");
                jsonObject = response;
                progressDialog.hide();
                initImgBitmaps(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: error in getting more places.");
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void initImgBitmaps(JSONObject jsonObject){
        Log.d(TAG, "initImgBitmaps: preparing bitmaps.");

        // print json object
        try {
            Log.d(TAG, jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get next page token
        next = findViewById(R.id.next);
        String nextPageToken = null;
        if (jsonObject.has("next_page_token")){
            try {
                nextPageToken = jsonObject.getString("next_page_token");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (nextPageToken != null){
            next.setClickable(true);
            next.setTextColor(Color.BLACK);
        }
        else {
            next.setClickable(false);
            next.setTextColor(Color.GRAY);
        }

        // make previous button clickable
        previous = findViewById(R.id.previous);
        if (pageNum > 1){
            previous.setClickable(true);
            previous.setTextColor(Color.BLACK);
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
                if (pageNum == 1){
                    mImgUrls1.add(curr_img);
                    mPlaceNames1.add(curr_name);
                    mPlaceLocations1.add(curr_loc);
                }
                else if (pageNum == 2){
                    mImgUrls2.add(curr_img);
                    mPlaceNames2.add(curr_name);
                    mPlaceLocations2.add(curr_loc);
                }
                else if (pageNum == 3) {
                    mImgUrls3.add(curr_img);
                    mPlaceNames3.add(curr_name);
                    mPlaceLocations3.add(curr_loc);
                }
                else {
                    Log.d(TAG, "initImgBitmaps: invalid page number");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        Log.d(TAG, "initRecyclerView: init recyclerview.");
        RecyclerView recyclerView = findViewById(R.id.search_recycler_view);
        RecyclerViewAdapter adapter = null;
        if (pageNum == 1){
            adapter = new RecyclerViewAdapter(this, mImgUrls1, mPlaceNames1, mPlaceLocations1);
        }
        else if (pageNum == 2){
            adapter = new RecyclerViewAdapter(this, mImgUrls2, mPlaceNames2, mPlaceLocations2);
        }
        else if (pageNum == 3){
            adapter = new RecyclerViewAdapter(this, mImgUrls3, mPlaceNames3, mPlaceLocations3);
        }
        else {
            Log.d(TAG, "initRecyclerView: invalid page number, won't make recyclerview");
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
