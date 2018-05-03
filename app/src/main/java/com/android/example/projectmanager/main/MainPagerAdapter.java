package com.android.example.projectmanager.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.android.example.projectmanager.main.projects.ProjectListFragment;
import com.android.example.projectmanager.main.quote.QuoteFragment;


public class MainPagerAdapter extends FragmentStatePagerAdapter {

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            Fragment fragment = new ProjectListFragment();
            return fragment;
        } else if (position == 1) {
            Fragment fragment = new QuoteFragment();
            return fragment;
        } else {
            Fragment fragment = new ProjectListFragment();
            return fragment;
        }
    }

    @Override
    public int getCount()  {
        return 2;
    }

}
