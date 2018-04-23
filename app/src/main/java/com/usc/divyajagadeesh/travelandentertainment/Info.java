package com.usc.divyajagadeesh.travelandentertainment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Info.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Info extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = "Info";

    public Info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Info.
     */
    // TODO: Rename and change types and number of parameters
    public static Info newInstance(String param1, String param2) {
        Info fragment = new Info();
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
        final View view =  inflater.inflate(R.layout.fragment_info, container, false);

        // get json from place details activity
        PlaceDetailsActivity activity = (PlaceDetailsActivity) getActivity();
        JSONObject jsonObject = activity.getMyData();
        try {
            Log.d(TAG, "onCreateView in Info: " + jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // address
        String address = "";
        try {
            address = jsonObject.getJSONObject("result").getString("formatted_address");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView info_address = (TextView) view.findViewById(R.id.info_address);
        info_address.setText(address);

        // phone number
        String phone_number = "";
        try {
            phone_number = jsonObject.getJSONObject("result").getString("formatted_phone_number");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView info_phone_number = (TextView) view.findViewById(R.id.info_phone_number);
        info_phone_number.setText(phone_number);
        if (phone_number != ""){
            Linkify.addLinks(info_phone_number, Linkify.ALL);
        }

        // price level
        String price_level = "";
        try {
            price_level = jsonObject.getJSONObject("result").getString("price_level");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView info_price_level = (TextView) view.findViewById(R.id.info_price_level);
        if (price_level != ""){
            int dollarSigns = Integer.parseInt(price_level);
            price_level = "";
            for (int i = 0; i < dollarSigns; i++){
                price_level = price_level + "$";
            }
        }
        info_price_level.setText(price_level);

        // rating
        String rating = "";
        try {
            rating = jsonObject.getJSONObject("result").getString("rating");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final RatingBar info_rating = (RatingBar) view.findViewById(R.id.info_rating);
        if (rating != ""){
            double ratingNum = Double.parseDouble(rating);
            info_rating.setRating((float) ratingNum);
        }
        else {
            info_rating.setVisibility(View.INVISIBLE);
        }

        // google page
        String google_page = "";
        try {
            google_page = jsonObject.getJSONObject("result").getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView info_google_page = (TextView) view.findViewById(R.id.info_google_page);
        if (google_page != ""){
            info_google_page.setClickable(true);
            info_google_page.setMovementMethod(LinkMovementMethod.getInstance());
            google_page = "<a href='" + google_page + "'>" + google_page + "</a>";
            info_google_page.setText(Html.fromHtml(google_page));
        }
        else {
            info_google_page.setText(google_page);
        }

        // website
        String website = "";
        try {
            website = jsonObject.getJSONObject("result").getString("website");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final TextView info_website = (TextView) view.findViewById(R.id.info_website);
        if (website != ""){
            info_website.setClickable(true);
            info_website.setMovementMethod(LinkMovementMethod.getInstance());
            website = "<a href='" + website + "'>" + website + "</a>";
            info_website.setText(Html.fromHtml(website));
        }
        else {
            info_website.setText(website);
        }


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
