package com.codertal.moviehub.features.movies;

import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.TabLayout;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codertal.moviehub.R;
import com.codertal.moviehub.features.movies.adapter.MoviePagerAdapter;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MoviesActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> mSupportFragmentInjector;

    private  MoviePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        //Add three fragment tabs for different filters
        mPagerAdapter = new MoviePagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(new MoviesFragment(), MoviesFilterType.POPULAR);
        mPagerAdapter.addFragment(new MoviesFragment(), MoviesFilterType.TOP_RATED);
        mPagerAdapter.addFragment(new MoviesFragment(), MoviesFilterType.FAVORITES);

        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mSupportFragmentInjector;
    }

    @VisibleForTesting
    @NonNull
    public CountingIdlingResource getIdlingResource() {
        return ((MoviesFragment)mPagerAdapter.getItem(0)).getIdlingResource();
    }

}
