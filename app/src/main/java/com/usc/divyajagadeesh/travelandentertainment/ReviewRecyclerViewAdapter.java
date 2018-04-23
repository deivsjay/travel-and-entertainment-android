package com.usc.divyajagadeesh.travelandentertainment;

import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ReviewRecyclerViewAdapter {

    private static final String TAG = "ReviewRecyclerViewAdapt";

    public class ViewHolder extends RecyclerViewAdapter.ViewHolder{

        ImageView image;
        TextView name;
        RatingBar rating;
        TextView date;
        TextView review;
        RelativeLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
