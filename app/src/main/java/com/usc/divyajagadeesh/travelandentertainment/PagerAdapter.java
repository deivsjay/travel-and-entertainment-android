package com.usc.divyajagadeesh.travelandentertainment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {

    int gnumOfTabs;

    public PagerAdapter(FragmentManager fm, int lnumOfTabs){

        super(fm);
        this.gnumOfTabs = lnumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch(position){

            case 0:
                Search search = new Search();
                return search;

            case 1:
                Favorites favorites = new Favorites();
                return favorites;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return gnumOfTabs;
    }
}
