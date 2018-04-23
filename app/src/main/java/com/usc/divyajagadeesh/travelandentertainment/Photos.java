package com.usc.divyajagadeesh.travelandentertainment;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.load.resource.bitmap.FitCenter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Photos.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Photos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Photos extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private GeoDataClient mGeoDataClient;
    private ArrayList<Bitmap> bitmaps = new ArrayList<>();

    private static final String TAG = "Photos";

    public Photos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Photos.
     */
    // TODO: Rename and change types and number of parameters
    public static Photos newInstance(String param1, String param2) {
        Photos fragment = new Photos();
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
        final View view = inflater.inflate(R.layout.fragment_photos, container, false);

        // get json from place details activity
        PlaceDetailsActivity activity = (PlaceDetailsActivity) getActivity();
        JSONObject jsonObject = activity.getMyData();
        try {
            Log.d(TAG, "onCreateView in Photo: " + jsonObject.toString(4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // place id
        String place_id = "";
        try {
            place_id = jsonObject.getJSONObject("result").getString("place_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // get photos
        getPhotos(place_id);

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

    // Request photos and metadata for the specified place.
    private void getPhotos(String placeId) {
//        final String placeId = "ChIJa147K9HX3IAR-lwiGIQv9i4";
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                // Get the list of photos.
                PlacePhotoMetadataResponse photos = task.getResult();
                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                // no. of photos
                final int length = photoMetadataBuffer.getCount();
                if (length > 0){
                    final TextView no_photos = getActivity().findViewById(R.id.no_photos);
                    no_photos.setVisibility(View.GONE);
                }
                for (int i = 0; i < length; i++){
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            bitmaps.add(bitmap);
                            Log.d(TAG, "getPhotos: bitmaps array size is " + bitmaps.size());
                            if (bitmaps.size() == length){
                                Log.d(TAG, "getPhotos: bitmaps TOTAL array size is " + bitmaps.size());
                                // test if photos appear
                                LinearLayout photos_list = (LinearLayout) getActivity().findViewById(R.id.photos_list);
                                for (int j = 0; j < bitmaps.size(); j++){
                                    ImageView image = new ImageView(getActivity());
                                    image.setImageBitmap(bitmaps.get(j));
                                    image.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    image.setAdjustViewBounds(true);
                                    image.setScaleType(ImageView.ScaleType.FIT_CENTER);
                                    if (j == bitmaps.size() - 1){
                                        image.setPadding(40, 40, 40, 40);
                                    }
                                    else {
                                        image.setPadding(40, 40, 40, 0);
                                    }
                                    photos_list.addView(image);
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}
