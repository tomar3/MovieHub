package com.tomar.udacity.popularmovies;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tomar.udacity.popularmovies.adapters.MovieGridAdapter;
import com.tomar.udacity.popularmovies.fragments.MoviesGridFragment;
import com.tomar.udacity.popularmovies.tasks.MovieQueryTask;
import com.tomar.udacity.popularmovies.utilities.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.FAVORITES;
import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.POPULAR;
import static com.tomar.udacity.popularmovies.fragments.MoviesGridFragment.TOP_RATED;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }

    private void setupViewPager(ViewPager viewPager) {
        MovieGridViewPager adapter = new MovieGridViewPager(getSupportFragmentManager());
        adapter.addFragment(new MoviesGridFragment(), POPULAR);
        adapter.addFragment(new MoviesGridFragment(), TOP_RATED);
        adapter.addFragment(new MoviesGridFragment(), FAVORITES);
        viewPager.setAdapter(adapter);

    }


}
