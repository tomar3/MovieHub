package com.codertal.moviehub;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.codertal.moviehub.R;
import com.codertal.moviehub.fragments.MoviesGridFragment;


import static com.codertal.moviehub.fragments.MoviesGridFragment.FAVORITES;
import static com.codertal.moviehub.fragments.MoviesGridFragment.POPULAR;
import static com.codertal.moviehub.fragments.MoviesGridFragment.TOP_RATED;

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
        adapter.addFragment(new MoviesGridFragment(), MoviesGridFragment.POPULAR);
        adapter.addFragment(new MoviesGridFragment(), MoviesGridFragment.TOP_RATED);
        adapter.addFragment(new MoviesGridFragment(), MoviesGridFragment.FAVORITES);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);
    }


}
