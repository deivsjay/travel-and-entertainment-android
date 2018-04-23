package com.usc.divyajagadeesh.travelandentertainment;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PlaceDetailsActivity extends AppCompatActivity implements
        Info.OnFragmentInteractionListener, Photos.OnFragmentInteractionListener,
        Map.OnFragmentInteractionListener, Reviews.OnFragmentInteractionListener {

    private static final String TAG = "PlaceDetailsActivity";
    public JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_details);

        // get json object from results activity
        String jsonObj = "";
        if (getIntent().hasExtra("json")){
            jsonObj = getIntent().getStringExtra("json");
        }
        // put json string into json object
        try {
            jsonObject = new JSONObject(jsonObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // print json object
        try {
            Log.d(TAG, "onCreate: place_id is " + jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // name
        String name = "";
        try {
            name = jsonObject.getJSONObject("result").getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView place_name = (TextView) findViewById(R.id.place_name);
        place_name.setText(name);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.placeDetails_tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("INFO").setIcon(R.drawable.info_outline));
        tabLayout.addTab(tabLayout.newTab().setText("PHOTOS").setIcon(R.drawable.photos));
        tabLayout.addTab(tabLayout.newTab().setText("MAP").setIcon(R.drawable.maps));
        tabLayout.addTab(tabLayout.newTab().setText("REVIEWS").setIcon(R.drawable.review));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager)findViewById(R.id.placeDetails_viewpager);
        final PlaceDetailsPagerAdapter adapter = new PlaceDetailsPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // back button on place details page
        ImageButton back = (ImageButton)findViewById(R.id.placeDetails_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public JSONObject getMyData(){
        return jsonObject;
    }
}
