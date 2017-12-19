package com.codertal.moviehub;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codertal.moviehub.features.movies.MoviesFragment;

import javax.inject.Inject;

import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;

public class MainActivity extends AppCompatActivity implements HasSupportFragmentInjector {

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
        MovieGridViewPager adapter = new MovieGridViewPager(getSupportFragmentManager());
        adapter.addFragment(new MoviesFragment(), MoviesFragment.POPULAR);
        adapter.addFragment(new MoviesFragment(), MoviesFragment.TOP_RATED);
        adapter.addFragment(new MoviesFragment(), MoviesFragment.FAVORITES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }


    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return mSupportFragmentInjector;
    }

}
