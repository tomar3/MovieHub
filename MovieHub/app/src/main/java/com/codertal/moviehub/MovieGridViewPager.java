package com.codertal.moviehub;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.codertal.moviehub.fragments.MoviesGridFragment;

import java.util.ArrayList;
import java.util.List;

import static com.codertal.moviehub.fragments.MoviesGridFragment.SORT_TYPE;

public class MovieGridViewPager extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public MovieGridViewPager(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        Bundle sortBundle = new Bundle();
        sortBundle.putString(MoviesGridFragment.SORT_TYPE, title);

        fragment.setArguments(sortBundle);
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
