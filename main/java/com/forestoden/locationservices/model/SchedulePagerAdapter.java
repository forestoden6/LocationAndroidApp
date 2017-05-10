package com.forestoden.locationservices.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.forestoden.locationservices.fragments.BSLScheduleFragment;
import com.forestoden.locationservices.fragments.MFLScheduleFragment;

/**
 * Created by ForestOden on 4/19/2017.
 * Project: LocationServices.
 */

public class SchedulePagerAdapter extends FragmentStatePagerAdapter{

    private int mNumOfTabs = 2;

    public SchedulePagerAdapter(FragmentManager fm) { super(fm); }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MFLScheduleFragment();
            case 1:
                return new BSLScheduleFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
