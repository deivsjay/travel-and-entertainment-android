package com.usc.divyajagadeesh.travelandentertainment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import javax.sql.RowSetEvent;

public class PlaceDetailsPagerAdapter extends FragmentStatePagerAdapter {

    int mNoOfTabs;

    public PlaceDetailsPagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                Info info = new Info();
                return info;
            case 1:
                Photos photos = new Photos();
                return photos;
            case 2:
                Map map = new Map();
                return map;
            case 3:
                Reviews reviews = new Reviews();
                return reviews;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
