package com.codertal.moviehub.features.movies;

import android.support.design.widget.TabLayout;
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
        MoviePagerAdapter adapter = new MoviePagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MoviesFragment(), MoviesFilterType.POPULAR);
        adapter.addFragment(new MoviesFragment(), MoviesFilterType.TOP_RATED);
        adapter.addFragment(new MoviesFragment(), MoviesFilterType.FAVORITES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mSupportFragmentInjector;
    }

}
