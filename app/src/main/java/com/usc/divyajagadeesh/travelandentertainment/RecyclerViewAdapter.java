package com.usc.divyajagadeesh.travelandentertainment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter";

    private ArrayList<String> mImages = new ArrayList<>();
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mLocations = new ArrayList<>();
    private ArrayList<String> mPlaceIds = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter(Context mContext, ArrayList<String> mImages, ArrayList<String> mNames,
                               ArrayList<String> mLocations, ArrayList<String> mPlaceIds) {
        this.mImages = mImages;
        this.mNames = mNames;
        this.mLocations = mLocations;
        this.mContext = mContext;
        this.mPlaceIds = mPlaceIds;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_placeitem, parent, false);
         ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));
        holder.location.setText(mLocations.get(position));
        final String placeid = mPlaceIds.get(position);
        holder.placeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // progress dialog while data is loading
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setMessage("Fetching results");
                progressDialog.show();
                // get place details
                String url = "https://maps.googleapis.com/maps/api/place/details/json?" +
                        "placeid=" + placeid +
                        "&key=AIzaSyBplvW6nbbCeqKRlqT0pz9YsrYTYPWFowI";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // transition to place details page
                        Intent intent = new Intent(mContext, PlaceDetailsActivity.class);
                        intent.putExtra("json", response.toString());
                        progressDialog.hide();
                        mContext.startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                MySingleton.getInstance(mContext).addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name;
        TextView location;
        RelativeLayout placeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.results_image);
            name = itemView.findViewById(R.id.results_name);
            location = itemView.findViewById(R.id.results_location);
            placeLayout = itemView.findViewById(R.id.place_layout);
        }
    }

}
