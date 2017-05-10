package com.forestoden.locationservices.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.forestoden.locationservices.fragments.BSLServiceAdvisoryFragment;
import com.forestoden.locationservices.fragments.MFLServiceAdvisoryFragment;

/**
 * Created by ForestOden on 4/16/2017.
 * Project: LocationServices.
 */

public class ServiceAdvisoryPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs = 2;

    public ServiceAdvisoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MFLServiceAdvisoryFragment();
            case 1:
                return new BSLServiceAdvisoryFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
