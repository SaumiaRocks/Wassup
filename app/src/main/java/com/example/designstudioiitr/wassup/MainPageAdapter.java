package com.example.designstudioiitr.wassup;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Saumia Singhal on 3/28/2018.
 */

public class MainPageAdapter extends FragmentPagerAdapter {

    private final static int NUM_FRAGMENTS = 3;

    public MainPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0 :
                return (new ChatFragment());
            case 1 :
                return (new FriendsFragment());
            case 2 :
                return (new ChatRequestsFragment());
            default :
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_FRAGMENTS;
    }
}
