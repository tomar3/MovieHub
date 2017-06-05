package com.tomar.udacity.popularmovies;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.widget.ImageView;

import com.tomar.udacity.popularmovies.fragments.MoviesGridFragment;


import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.FAVORITES;
import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.POPULAR;
import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.TOP_RATED;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        //Add three fragment tabs for different filters
        MovieGridViewPager adapter = new MovieGridViewPager(getSupportFragmentManager());
        adapter.addFragment(new MoviesGridFragment(), POPULAR);
        adapter.addFragment(new MoviesGridFragment(), TOP_RATED);
        adapter.addFragment(new MoviesGridFragment(), FAVORITES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }


}
