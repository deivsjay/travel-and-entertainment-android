package com.usc.divyajagadeesh.travelandentertainment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Map#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Map extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private GoogleMap mMap;
    private double latitude;
    private double longitude;
    private static final int LOCATION_REQUEST = 500;
    ArrayList<LatLng> listPoints;
    private String place_id;
    private JSONObject directionsObject;

    // autocomplete
    private AutoCompleteTextView autoCompleteTextView;
    private PlaceAutocompleteAdapter placeAutoCompleteAdapter;
    private GeoDataClient mGoogleApiClient;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(new LatLng(-140, -168), new LatLng(71, 136));

    private static final String TAG = "Map";

    public Map() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Map.
     */
    // TODO: Rename and change types and number of parameters
    public static Map newInstance(String param1, String param2) {
        Map fragment = new Map();
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
        final View view = inflater.inflate(R.layout.fragment_map, container, false);

        // category spinner
        final Spinner spinner = (Spinner) view.findViewById(R.id.map_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.travel_modes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // autocomplete
        autoCompleteTextView = view.findViewById(R.id.map_from);
        placeAutoCompleteAdapter = new PlaceAutocompleteAdapter(getActivity(),
                Places.getGeoDataClient(getActivity(), null),
                LAT_LNG_BOUNDS,
                null);

        autoCompleteTextView.setAdapter(placeAutoCompleteAdapter);

        // get content from search results activity
        PlaceDetailsActivity activity = (PlaceDetailsActivity) getActivity();
        JSONObject jsonObject = activity.getMyData();

        // get place id
        try {
            place_id = jsonObject.getJSONObject("result").getString("place_id");
            Log.d(TAG, "onCreateView in Map (place_id): " + place_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get lat and lon
        try {
            latitude = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            longitude = jsonObject.getJSONObject("result").getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // instantiate new arraylist of points
        listPoints = new ArrayList<>();

        // map stuff
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // something selected on spinner
        final AutoCompleteTextView map_from = (AutoCompleteTextView) view.findViewById(R.id.map_from);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                // get from and mode
                String modeSelected = spinner.getSelectedItem().toString().toLowerCase();
                Log.d(TAG, "onItemSelected (mode): " + modeSelected);
                String from = map_from.getText().toString();
                Log.d(TAG, "onItemSelected (from): " + from);
                // make api call
                if (!from.equals("")){
                    String url = null;
                    try {
                        url = "https://maps.googleapis.com/maps/api/directions/json?" +
                                "origin=" + URLEncoder.encode(from, "UTF-8") +
                                "&destination=place_id:" + place_id +
                                "&mode=" + modeSelected +
                                "&key=AIzaSyBplvW6nbbCeqKRlqT0pz9YsrYTYPWFowI";
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "onItemSelected: " + url);
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Log.d(TAG, "onResponse: " + response.toString(4));
                                directionsObject = response;
                                TaskParser taskParser = new TaskParser();
                                mMap.clear();
                                taskParser.execute("divya");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return view;
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.setMinZoomPreference(12.0f);
        mMap.setMaxZoomPreference(16.0f);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.getUiSettings().setZoomControlsEnabled(true);

        // get directions
//        mMap.getUiSettings().setZoomControlsEnabled(true);
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
//            return;
//        }
//        mMap.setMyLocationEnabled(true);
//        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
//            @Override
//            public void onMapLongClick(LatLng latLng) {
//                // reset marker when already 2
//                if (listPoints.size() == 2){
//                    listPoints.clear();
//                    mMap.clear();
//                }
//                // save first point select
//                listPoints.add(latLng);
//                // create marker
//                MarkerOptions marker = new MarkerOptions();
//                marker.position(latLng);
//
//                if (listPoints.size() == 1){
//                    // add first marker to the map
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
//                }
//                else {
//                    // add second marker to the map
//                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
//                }
//                mMap.addMarker(marker);
//
//                // TODO: request get directions code below
//                if (listPoints.size() == 2){
//                    // Create URL to get request from first marker to a second marker
//                    String url = getRequestUrl(listPoints.get(0), listPoints.get(1));
//                }
//            }
//        });
    }

//    private String getRequestUrl(LatLng origin, LatLng dest) {
//      // get url with both destinations
//    }

//    @SuppressLint("MissingPermission")
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch(requestCode){
//            case LOCATION_REQUEST:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    mMap.setMyLocationEnabled(true);
//                }
//                break;
//        }
//    }

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

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> > {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            List<List<HashMap<String, String>>> routes = null;
            DirectionsParser directionsParser = new DirectionsParser();
            routes = directionsParser.parse(directionsObject);
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            // get list route and display it into the map
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point: path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lon"));

                    points.add(new LatLng(lat,lon));
                }

                // adjust markers
                int length = points.size();
                LatLng start = (LatLng) points.get(0);
                LatLng end = (LatLng) points.get(length-1);
                mMap.addMarker(new MarkerOptions().position(start));
                mMap.addMarker(new MarkerOptions().position(end));

                // make polyline
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }

            if (polylineOptions != null){
                mMap.addPolyline(polylineOptions);
            }
            else {
                Toast.makeText(getActivity(), "Direction not found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
