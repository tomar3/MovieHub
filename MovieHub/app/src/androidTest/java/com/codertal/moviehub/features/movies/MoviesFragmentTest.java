package com.codertal.moviehub.features.movies;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.codertal.moviehub.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.Espresso.onView;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;

import android.support.test.espresso.contrib.RecyclerViewActions;



@RunWith(AndroidJUnit4.class)
public class MoviesFragmentTest {
    @Rule
    public ActivityTestRule<MoviesActivity> mActivityTestRule =
            new ActivityTestRule<>(MoviesActivity.class);

    private IdlingResource mIdlingResource;

    @Before
    public void registerIdlingResource() {
        mIdlingResource = mActivityTestRule.getActivity().getIdlingResource();

        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Test
    public void clickOnMovie_ShouldOpenDetailUi() {

        onView(allOf((withId(R.id.rv_movies)), isDisplayed()))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.tv_title))
                .check(matches(withText("The Movie 1")));
    }

    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }
}
