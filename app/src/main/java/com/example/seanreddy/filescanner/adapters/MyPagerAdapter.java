package com.example.seanreddy.filescanner.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.example.seanreddy.filescanner.fragments.AverageFragment;
import com.example.seanreddy.filescanner.fragments.BiggerFilesFragment;
import com.example.seanreddy.filescanner.fragments.ExtensionFragment;
import com.example.seanreddy.filescanner.util.FileInfo;

/*
* Adapter for ViewPager
* */
public class MyPagerAdapter extends FragmentPagerAdapter {
    private static final String BIGFILES="Big Files";
    private static final String AVERAGE="Average ";
    private static final String TOPEXTENSIONS="Top Extensions";
    SparseArray<Fragment> registerdFragments = new SparseArray<Fragment>();
    private BiggerFilesFragment biggerFilesFragment;
    private ExtensionFragment  extensionFragment;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
        populateFragments();
    }

    private void populateFragments() {
        biggerFilesFragment = new BiggerFilesFragment();
        extensionFragment = new ExtensionFragment();
        registerdFragments.put(0,biggerFilesFragment);
        registerdFragments.put(1,new AverageFragment());
        registerdFragments.put(2,extensionFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return registerdFragments.get(position);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String pageTitle;
        switch (position){
            case  0:
                pageTitle=BIGFILES;
                break;
            case  1:
                pageTitle=AVERAGE;
                break;
            case  2:
                pageTitle=TOPEXTENSIONS;
                break;
            default:
                pageTitle = null;
        }
        return pageTitle;
    }

    /*
    *
    * */
    public Fragment getRegisteredFragment(int pos) {
        return registerdFragments.get(pos);
    }
}
