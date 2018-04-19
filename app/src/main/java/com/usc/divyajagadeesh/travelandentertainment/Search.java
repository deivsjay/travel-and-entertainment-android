package com.usc.divyajagadeesh.travelandentertainment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static android.view.View.VISIBLE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Search.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Search#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Search extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText text;

    // current location
    double latitude = 34.0266;
    double longitude = -118.2831;

    private OnFragmentInteractionListener mListener;

    public Search() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Search.
     */
    // TODO: Rename and change types and number of parameters
    public static Search newInstance(String param1, String param2) {
        Search fragment = new Search();
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
        final View view =  inflater.inflate(R.layout.fragment_search, container, false);

        // category spinner
        final Spinner spinner = (Spinner) view.findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.category_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // enable and disable location autocomplete
        final RadioGroup location_radio = (RadioGroup)view.findViewById(R.id.location_radiogroup);
        final RadioButton current_location = (RadioButton)view.findViewById(R.id.current_location);
        final AutoCompleteTextView location_text = (AutoCompleteTextView)view.findViewById(R.id.location_autocomplete);
        final TextView location_error = view.findViewById(R.id.location_error);
        location_radio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (current_location.isChecked()){
                    location_text.setText("");
                    location_text.setEnabled(false);
                    location_error.setVisibility(View.INVISIBLE);
                }
                else {
                    location_text.setEnabled(true);
                }
            }
        });

        // search button clicked
        final Button search = (Button) view.findViewById(R.id.search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("divya", "search button pressed");
                int validated = 1;

                // keyword validation check
                final TextView keyword_check = (TextView)view.findViewById(R.id.keyword_edittext);
                final TextView keyword_error = (TextView)view.findViewById(R.id.keyword_error);
                if (keyword_check.getText().toString().trim().equals("")){
                    Log.d("keyword", "keyword NOT fine");
                    keyword_error.setVisibility(VISIBLE);
                    validated = 0;
                }
                else {
                    Log.d("keyword", "keyword fine");
                    keyword_error.setVisibility(View.INVISIBLE);
                    validated = 1;
                }

                // location text validation check
                final RadioButton location_radio = (RadioButton)view.findViewById(R.id.other_location);
                final EditText location_text = (EditText)view.findViewById(R.id.location_autocomplete);
                final TextView location_error = (TextView)view.findViewById(R.id.location_error);
                if (location_radio.isChecked() && location_text.getText().toString().trim().equals("")){
                    location_error.setVisibility(VISIBLE);
                    validated = 0;
                }
                else {
                    location_error.setVisibility(View.INVISIBLE);
                    validated = 1;
                }

                if (validated == 1){

                    // get form fields
                    String keywordInput = keyword_check.getText().toString();
                    String categoryInput = spinner.getSelectedItem().toString();
                    final TextView distance = (TextView)view.findViewById(R.id.distance_edittext);
                    String distanceString = distance.getText().toString();
                    double distanceNum = 10;
                    String locationInput = location_text.getText().toString();


                    // distance value
                    if (!distanceString.equals("")){
                        distanceNum = Integer.parseInt(distanceString);
                    }
                    distanceNum = distanceNum * 1609.344;

                    // make request queue for api calls
                    RequestQueue queue = MySingleton.getInstance(getActivity().getApplicationContext()).
                            getRequestQueue();

                    // current location or user inputed location
                    if (!locationInput.equals("")){
                        try {
                            locationInput = URLEncoder.encode(locationInput, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        // call geolocation api
                        String url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com/geoloc?location=" + locationInput;
                        Log.d("location url", url);
                        JsonObjectRequest geolocation = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray results = response.getJSONArray("results");
                                    JSONObject resultsObj = results.getJSONObject(0);
                                    JSONObject location = resultsObj.getJSONObject("geometry").getJSONObject("location");
                                    double latFromJSON = location.getDouble("lat");
                                    double lngFromJSON = location.getDouble("lng");
                                    latitude = latFromJSON;
                                    longitude = lngFromJSON;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("geolocation", "Something went wrong");
                            }
                        });
                        MySingleton.getInstance(getActivity()).addToRequestQueue(geolocation);
                    }

                    // output all values
                    Log.d("places", "keyword: " + keywordInput);
                    Log.d("places", "cateogry: " + categoryInput);
                    Log.d("places", "distance: " + distanceNum);
                    Log.d("places", "location: " + locationInput);
                    Log.d("places", "latitude: " + latitude);
                    Log.d("places", "longitude: " + longitude);

                    // make places api call
                    String url = "http://travelandentertainment.us-east-2.elasticbeanstalk.com/places?keyword=" +
                            keywordInput + "&type=" + categoryInput + "&radius=" + distanceNum +
                            "&lat=" + latitude + "&lon=" + longitude;
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            try {
                                Log.d("response", response.toString(4));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // transition to search results page
                            String json = response.toString();
                            Intent intent = new Intent(getActivity(), ResultsActivity.class);
                            intent.putExtra("json", json);
                            startActivity(intent);

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("response", "Something went wrong");
                            error.printStackTrace();
                        }
                    });

                    MySingleton.getInstance(getActivity()).addToRequestQueue(jsonObjectRequest);
                }

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


}
