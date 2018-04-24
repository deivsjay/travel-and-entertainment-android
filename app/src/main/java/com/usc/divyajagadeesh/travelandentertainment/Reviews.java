package com.usc.divyajagadeesh.travelandentertainment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Reviews.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Reviews#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reviews extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // default google reviews
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mPersonNames = new ArrayList<>();
    private ArrayList<Integer> mRatings = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    private ArrayList<String> mReviews = new ArrayList<>();
    private ArrayList<Integer> mUnixTimestamps = new ArrayList<>();
    private ArrayList<String> mAuthorUrls = new ArrayList<>();

    // default yelp reviews
    private ArrayList<String> mImageUrlsY = new ArrayList<>();
    private ArrayList<String> mPersonNamesY = new ArrayList<>();
    private ArrayList<Integer> mRatingsY = new ArrayList<>();
    private ArrayList<String> mDatesY = new ArrayList<>();
    private ArrayList<String> mReviewsY = new ArrayList<>();
    private ArrayList<Integer> mUnixTimestampsY = new ArrayList<>();
    private ArrayList<String> mAuthorUrlsY = new ArrayList<>();

    // all other google reviews
    private ArrayList<String> mImageUrlsSorted = new ArrayList<>();
    private ArrayList<String> mPersonNamesSorted = new ArrayList<>();
    private ArrayList<Integer> mRatingsSorted = new ArrayList<>();
    private ArrayList<String> mDatesSorted = new ArrayList<>();
    private ArrayList<String> mReviewsSorted = new ArrayList<>();
    private ArrayList<Integer> mUnixTimestampsSorted = new ArrayList<>();
    private ArrayList<String> mAuthorUrlsSorted = new ArrayList<>();

    // all other yelp reviews
    private ArrayList<String> mImageUrlsSortedY = new ArrayList<>();
    private ArrayList<String> mPersonNamesSortedY = new ArrayList<>();
    private ArrayList<Integer> mRatingsSortedY = new ArrayList<>();
    private ArrayList<String> mDatesSortedY = new ArrayList<>();
    private ArrayList<String> mReviewsSortedY = new ArrayList<>();
    private ArrayList<Integer> mUnixTimestampsSortedY = new ArrayList<>();
    private ArrayList<String> mAuthorUrlsSortedY = new ArrayList<>();

    // review object
    private ArrayList<ReviewObject> allReviews = new ArrayList<>();
    private ArrayList<ReviewObject> allReviewsY = new ArrayList<>();

    // yelp id
    String yelpId;
    JSONObject yelpResponse;
    int yelpFound;
    int googleFound;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "Reviews";

    public Reviews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reviews.
     */
    // TODO: Rename and change types and number of parameters
    public static Reviews newInstance(String param1, String param2) {
        Reviews fragment = new Reviews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_reviews, container, false);
        final Context mContext = getActivity();

        // spinners
        final Spinner googleYelp = (Spinner) view.findViewById(R.id.reviews_gy);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.google_yelp, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        googleYelp.setAdapter(adapter);
        final Spinner order = (Spinner) view.findViewById(R.id.reviews_order);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.order, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        order.setAdapter(adapter1);

        // get json from place details activity
        PlaceDetailsActivity activity = (PlaceDetailsActivity) getActivity();
        JSONObject jsonObject = activity.getMyData();
        try {
            Log.d(TAG, "onCreateView in Reviews: " + jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get data into arrays
        int no_reviews = initImageBitmaps(jsonObject);
        final TextView noReviews = (TextView)view.findViewById(R.id.no_reviews);
        if (no_reviews == 0){
            // hide no reviews text view
            googleFound = 1;
            noReviews.setVisibility(View.GONE);
        }

        if (no_reviews == 0){
            // get first yelp url
            String url = firstYelpUrl(jsonObject);
            Log.d(TAG, "onCreateView: first Yelp url " + url);

            // call first yelp api
            yelpId = "";
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if (response.getJSONArray("businesses").length() > 0){
                            Log.d(TAG, "onResponse: there are yelp reviews!");
                            yelpId = response.getJSONArray("businesses").getJSONObject(0).getString("id");

                            String url2 = "http://travelandentertainment.us-east-2.elasticbeanstalk.com/yelpreviews?" +
                                    "id=" + yelpId;
                            Log.d(TAG, "onResponse: " + url2);
                            JsonObjectRequest jsonObjectRequest1 = new JsonObjectRequest(Request.Method.GET, url2, null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d(TAG, "onResponse: " + response);
                                    yelpResponse = response;
                                    yelpFound = 1;
                                    initYelpArrayLists();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            });
                            MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest1);

                        }
                        else {
                            Log.d(TAG, "onResponse: there are NO yelp reviews");
                            yelpFound = 0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
            MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
        }

        // initialize recycler view
        final RecyclerView recyclerView = view.findViewById(R.id.google_default);
        ReviewRecyclerViewAdapter adapter2 = new ReviewRecyclerViewAdapter(mContext, mImageUrls, mPersonNames, mRatings, mDates, mReviews, mAuthorUrls);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        recyclerView.setAdapter(adapter2);

        // spinners that control all reviews
        final Spinner reviewsOrder = view.findViewById(R.id.reviews_order);
        final Spinner gORy = (Spinner)view.findViewById(R.id.reviews_gy);

        // on click listener when review change occurs
        reviewsOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String selectedOrder = reviewsOrder.getSelectedItem().toString();
                String selectedType = gORy.getSelectedItem().toString();

                if (selectedType.equals("Google Reviews")){
                    if (googleFound == 1) {
                        noReviews.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (!selectedOrder.equals("Default Order")) {
                            // least recent
                            if (selectedOrder.equals("Least Recent")) {
                                // least recent --> google
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Ascending date
                                    }
                                });
                            }
                            // most recent
                            else if (selectedOrder.equals("Most Recent")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Descending date
                                    }
                                });
                            }
                            // lowest rating
                            else if (selectedOrder.equals("Lowest Rating")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.rating - p2.rating; // Ascending rating
                                    }
                                });
                            }
                            // highest rating
                            else if (selectedOrder.equals("Highest Rating")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.rating - p2.rating; // Descending rating
                                    }
                                });
                            }
                            // put into arraylists
                            int allReviewsLength = allReviews.size();
                            mImageUrlsSorted.clear();
                            mPersonNamesSorted.clear();
                            mRatingsSorted.clear();
                            mDatesSorted.clear();
                            mReviewsSorted.clear();
                            mUnixTimestampsSorted.clear();
                            mAuthorUrlsSorted.clear();
                            for (int j = 0; j < allReviewsLength; j++) {
                                ReviewObject reviewObject = allReviews.get(j);
                                mImageUrlsSorted.add(reviewObject.imgUrl);
                                mPersonNamesSorted.add(reviewObject.name);
                                mRatingsSorted.add(reviewObject.rating);
                                mDatesSorted.add(reviewObject.date);
                                mReviewsSorted.add(reviewObject.review);
                                mUnixTimestampsSorted.add(reviewObject.unixTimestamp);
                                mAuthorUrlsSorted.add(reviewObject.authorUrl);
                            }
//                    RecyclerView recyclerViewLeast = view.findViewById(R.id.google_default);
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsSorted, mPersonNamesSorted, mRatingsSorted, mDatesSorted, mReviewsSorted, mAuthorUrlsSorted);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        } else {
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrls, mPersonNames, mRatings, mDates, mReviews, mAuthorUrls);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        }
                    }
                    else {
                        noReviews.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                else if (selectedType.equals("Yelp Reviews")){
                    if (yelpFound == 1) {
                        noReviews.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (!selectedOrder.equals("Default Order")) {
                            // least recent
                            if (selectedOrder.equals("Least Recent")) {
                                // least recent --> google
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Ascending date
                                    }
                                });
                            }
                            // most recent
                            else if (selectedOrder.equals("Most Recent")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Descending date
                                    }
                                });
                            }
                            // lowest rating
                            else if (selectedOrder.equals("Lowest Rating")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.rating - p2.rating; // Ascending rating
                                    }
                                });
                            }
                            // highest rating
                            else if (selectedOrder.equals("Highest Rating")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.rating - p2.rating; // Descending rating
                                    }
                                });
                            }
                            // put into arraylists
                            int allReviewsYLength = allReviewsY.size();
                            mImageUrlsSortedY.clear();
                            mPersonNamesSortedY.clear();
                            mRatingsSortedY.clear();
                            mDatesSortedY.clear();
                            mReviewsSortedY.clear();
                            mUnixTimestampsSortedY.clear();
                            mAuthorUrlsSortedY.clear();
                            for (int j = 0; j < allReviewsYLength; j++) {
                                ReviewObject reviewObject = allReviewsY.get(j);
                                mImageUrlsSortedY.add(reviewObject.imgUrl);
                                mPersonNamesSortedY.add(reviewObject.name);
                                mRatingsSortedY.add(reviewObject.rating);
                                mDatesSortedY.add(reviewObject.date);
                                mReviewsSortedY.add(reviewObject.review);
                                mUnixTimestampsSortedY.add(reviewObject.unixTimestamp);
                                mAuthorUrlsSortedY.add(reviewObject.authorUrl);
                            }
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsSortedY, mPersonNamesSortedY, mRatingsSortedY, mDatesSortedY, mReviewsSortedY, mAuthorUrlsSortedY);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        } else {
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsY, mPersonNamesY, mRatingsY, mDatesY, mReviewsY, mAuthorUrlsY);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        }
                    }
                    else {
                        noReviews.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // on click listener when between google and yelp reviews switch

        gORy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {



                String selectedOrder = reviewsOrder.getSelectedItem().toString();
                String selectedType = gORy.getSelectedItem().toString();

                if (selectedType.equals("Google Reviews")){
                    if (googleFound == 1) {
                        noReviews.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (!selectedOrder.equals("Default Order")) {
                            // least recent
                            if (selectedOrder.equals("Least Recent")) {
                                // least recent --> google
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Ascending date
                                    }
                                });
                            }
                            // most recent
                            else if (selectedOrder.equals("Most Recent")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Descending date
                                    }
                                });
                            }
                            // lowest rating
                            else if (selectedOrder.equals("Lowest Rating")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.rating - p2.rating; // Ascending rating
                                    }
                                });
                            }
                            // highest rating
                            else if (selectedOrder.equals("Highest Rating")) {
                                Collections.sort(allReviews, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.rating - p2.rating; // Descending rating
                                    }
                                });
                            }
                            // put into arraylists
                            int allReviewsLength = allReviews.size();
                            mImageUrlsSorted.clear();
                            mPersonNamesSorted.clear();
                            mRatingsSorted.clear();
                            mDatesSorted.clear();
                            mReviewsSorted.clear();
                            mUnixTimestampsSorted.clear();
                            mAuthorUrlsSorted.clear();
                            for (int j = 0; j < allReviewsLength; j++) {
                                ReviewObject reviewObject = allReviews.get(j);
                                mImageUrlsSorted.add(reviewObject.imgUrl);
                                mPersonNamesSorted.add(reviewObject.name);
                                mRatingsSorted.add(reviewObject.rating);
                                mDatesSorted.add(reviewObject.date);
                                mReviewsSorted.add(reviewObject.review);
                                mUnixTimestampsSorted.add(reviewObject.unixTimestamp);
                                mAuthorUrlsSorted.add(reviewObject.authorUrl);
                            }
//                    RecyclerView recyclerViewLeast = view.findViewById(R.id.google_default);
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsSorted, mPersonNamesSorted, mRatingsSorted, mDatesSorted, mReviewsSorted, mAuthorUrlsSorted);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        } else {
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrls, mPersonNames, mRatings, mDates, mReviews, mAuthorUrls);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        }
                    }
                    else {
                        noReviews.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }
                else if (selectedType.equals("Yelp Reviews")){
                    if (yelpFound == 1) {
                        noReviews.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        if (!selectedOrder.equals("Default Order")) {
                            // least recent
                            if (selectedOrder.equals("Least Recent")) {
                                // least recent --> google
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Ascending date
                                    }
                                });
                            }
                            // most recent
                            else if (selectedOrder.equals("Most Recent")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.unixTimestamp - p2.unixTimestamp; // Descending date
                                    }
                                });
                            }
                            // lowest rating
                            else if (selectedOrder.equals("Lowest Rating")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p1, ReviewObject p2) {
                                        return p1.rating - p2.rating; // Ascending rating
                                    }
                                });
                            }
                            // highest rating
                            else if (selectedOrder.equals("Highest Rating")) {
                                Collections.sort(allReviewsY, new Comparator<ReviewObject>() {
                                    @Override
                                    public int compare(ReviewObject p2, ReviewObject p1) {
                                        return p1.rating - p2.rating; // Descending rating
                                    }
                                });
                            }
                            // put into arraylists
                            int allReviewsYLength = allReviewsY.size();
                            mImageUrlsSortedY.clear();
                            mPersonNamesSortedY.clear();
                            mRatingsSortedY.clear();
                            mDatesSortedY.clear();
                            mReviewsSortedY.clear();
                            mUnixTimestampsSortedY.clear();
                            mAuthorUrlsSortedY.clear();
                            for (int j = 0; j < allReviewsYLength; j++) {
                                ReviewObject reviewObject = allReviewsY.get(j);
                                mImageUrlsSortedY.add(reviewObject.imgUrl);
                                mPersonNamesSortedY.add(reviewObject.name);
                                mRatingsSortedY.add(reviewObject.rating);
                                mDatesSortedY.add(reviewObject.date);
                                mReviewsSortedY.add(reviewObject.review);
                                mUnixTimestampsSortedY.add(reviewObject.unixTimestamp);
                                mAuthorUrlsSortedY.add(reviewObject.authorUrl);
                            }
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsSortedY, mPersonNamesSortedY, mRatingsSortedY, mDatesSortedY, mReviewsSortedY, mAuthorUrlsSortedY);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        } else {
                            ReviewRecyclerViewAdapter adapterLeast = new ReviewRecyclerViewAdapter
                                    (mContext, mImageUrlsY, mPersonNamesY, mRatingsY, mDatesY, mReviewsY, mAuthorUrlsY);
                            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                            recyclerView.setAdapter(adapterLeast);
                        }
                    }
                    else {
                        noReviews.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
    }

    private int initImageBitmaps(JSONObject jsonObject){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps in reviews.");

        int noReviews = 1;

        if (jsonObject.has("result")){

            // get results
            JSONObject jsonObject1 = null;
            try {
                jsonObject1 = jsonObject.getJSONObject("result");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // review attributes
            JSONArray reviewsArray = null;
            String name = "";
            String image = "";
            int rating = 0;
            String date = "Apr 23 2018";
            int unixDate = 0;
            String review = "";
            int length = 0;
            String authorUrl = "";

            // get reviews
            if (jsonObject1.has("reviews")){
                noReviews = 0;
                try {
                    reviewsArray = jsonObject1.getJSONArray("reviews");
                    length = reviewsArray.length();

                    for (int i = 0; i < length; i++){
                        name = reviewsArray.getJSONObject(i).getString("author_name");
                        image = reviewsArray.getJSONObject(i).getString("profile_photo_url");
                        rating = reviewsArray.getJSONObject(i).getInt("rating");
                        review = reviewsArray.getJSONObject(i).getString("text");
                        authorUrl = reviewsArray.getJSONObject(i).getString("author_url");

                        // figure out date
                        unixDate = reviewsArray.getJSONObject(i).getInt("time");
                        java.util.Date time=new java.util.Date((long)unixDate*1000);
                        date = time.toString();

                        // add review objects
                        allReviews.add(new ReviewObject(image, name, rating, date, review, unixDate, authorUrl));

                        mImageUrls.add(image);
                        mPersonNames.add(name);
                        mRatings.add(rating);
                        mDates.add(date);
                        mReviews.add(review);
                        mUnixTimestamps.add(unixDate);
                        mAuthorUrls.add(authorUrl);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else {
                Log.d(TAG, "initImageBitmaps in Reviews: no reviews");
            }

        }
        else {
            Log.d(TAG, "initImageBitmaps in Reviews: no results");
        }


        return noReviews;

    }

    public String firstYelpUrl(JSONObject jsonObject){

        // get address components for yelp query
        JSONArray reviewsObj = null;
        String url = "";
        String name = "";
        try {
            name = jsonObject.getJSONObject("result").getString("name");
            reviewsObj = jsonObject.getJSONObject("result").getJSONArray("address_components");
            JSONObject first = reviewsObj.getJSONObject(0);
            String firstElement = first.getString("long_name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (reviewsObj.length() == 6){
            String city = "";
            try {
                city = reviewsObj.getJSONObject(0).getString("long_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String state = "";
            try {
                state = reviewsObj.getJSONObject(3).getString("short_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String country = "";
            try {
                country = reviewsObj.getJSONObject(4).getString("short_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com/yelpmatch?" +
                        "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&city=" + URLEncoder.encode(city, "UTF-8") +
                        "&state=" + URLEncoder.encode(state, "UTF-8") +
                        "&country=" + URLEncoder.encode(country, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else if (reviewsObj.length() == 8){
            String addressNumber = "";
            try {
                addressNumber = reviewsObj.getJSONObject(0).getString("long_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String streetName = "";
            try {
                streetName = reviewsObj.getJSONObject(1).getString("long_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String address1 = addressNumber + " " + streetName;
            String city = "";
            try {
                city = reviewsObj.getJSONObject(3).getString("long_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String state = "";
            try {
                state = reviewsObj.getJSONObject(5).getString("short_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String country = "";
            try {
                country = reviewsObj.getJSONObject(6).getString("short_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com/yelpmatch?" +
                        "name=" + URLEncoder.encode(name, "UTF-8") +
                        "&address1=" + URLEncoder.encode(address1, "UTF-8") +
                        "&city=" + URLEncoder.encode(city, "UTF-8") +
                        "&state=" + URLEncoder.encode(state, "UTF-8") +
                        "&country=" + URLEncoder.encode(country, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        else {
            Log.d(TAG, "onCreateView: need correct number of address components in Reviews");
        }

        return url;
    }

    public void initYelpArrayLists(){

        JSONArray yelpReviews = null;

        // get reviews
        try {
            yelpReviews = yelpResponse.getJSONArray("reviews");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // look at each review
        for (int j = 0; j < yelpReviews.length(); j++){
            JSONObject thisReview = null;
            try {
                thisReview = yelpReviews.getJSONObject(j);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String imgUrl = "";
            try {
                imgUrl = thisReview.getJSONObject("user").getString("image_url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mImageUrlsY.add(imgUrl);

            String name = "";
            try {
                name = thisReview.getJSONObject("user").getString("name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mPersonNamesY.add(name);

            int rating = 0;
            try {
                rating = thisReview.getInt("rating");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mRatingsY.add(rating);

            String date = "";
            try {
                date = thisReview.getString("time_created");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mDatesY.add(date);
            Log.d(TAG, "initYelpArrayLists (yelp review date): " + date);

            String review = "";
            try {
                review = thisReview.getString("text");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mReviewsY.add(review);

            String authorUrl = "";
            try {
                authorUrl = thisReview.getString("url");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mAuthorUrlsY.add(authorUrl);

            allReviewsY.add(new ReviewObject(imgUrl, name, rating, date, review, 0, authorUrl));
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ReviewObject {

        private String imgUrl;
        private String name;
        private int rating;
        private String date;
        private String review;
        private int unixTimestamp;
        private String authorUrl;

        public ReviewObject(String imgUrl, String name, int rating, String date, String review, int unixTimestamp, String authorUrl) {
            this.imgUrl = imgUrl;
            this.name = name;
            this.rating = rating;
            this.date = date;
            this.review = review;
            this.unixTimestamp = unixTimestamp;
            this.authorUrl = authorUrl;
        }
    }
}
