package com.usc.divyajagadeesh.travelandentertainment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReviewRecyclerViewAdapter extends RecyclerView.Adapter<ReviewRecyclerViewAdapter.ReviewViewHolder> {

    private static final String TAG = "ReviewRecyclerViewAdapt";

    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<String> mPersonNames = new ArrayList<>();
    private ArrayList<Integer> mRatings = new ArrayList<>();
    private ArrayList<String> mDates = new ArrayList<>();
    private ArrayList<String> mReviews = new ArrayList<>();
    private ArrayList<String> mAuthorUrls = new ArrayList<>();
    private Context mContext;

    public ReviewRecyclerViewAdapter(Context mContext, ArrayList<String> mImageUrls,
                                     ArrayList<String> mPersonNames, ArrayList<Integer> mRatings,
                                     ArrayList<String> mDates, ArrayList<String> mReviews,
                                     ArrayList<String> mAuthorUrls) {
        this.mImageUrls = mImageUrls;
        this.mPersonNames = mPersonNames;
        this.mRatings = mRatings;
        this.mDates = mDates;
        this.mReviews = mReviews;
        this.mContext = mContext;
        this.mAuthorUrls = mAuthorUrls;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_reviewitem, parent, false);
        ReviewViewHolder holder = new ReviewViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mPersonNames.get(position));
        holder.rating.setRating((float) mRatings.get(position));    // be aware of rating error
        holder.date.setText(mDates.get(position));
        holder.review.setText(mReviews.get(position));

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(mContext, mAuthorUrls.get(position), Toast.LENGTH_LONG).show();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mAuthorUrls.get(position)));
                mContext.startActivity(browserIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPersonNames.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        RatingBar rating;
        TextView date;
        TextView review;
        RelativeLayout parentLayout;

        public ReviewViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.review_image);
            name = itemView.findViewById(R.id.review_name);
            rating = itemView.findViewById(R.id.review_rating);
            date = itemView.findViewById(R.id.review_date);
            review = itemView.findViewById(R.id.review);
            parentLayout = itemView.findViewById(R.id.review_parent_layout);
        }
    }
}
