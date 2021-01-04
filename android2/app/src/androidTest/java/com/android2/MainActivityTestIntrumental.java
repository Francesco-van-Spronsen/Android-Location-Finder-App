package com.android2;

import android.view.View;

import androidx.test.espresso.Espresso;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainActivityTestIntrumental {
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =new ActivityTestRule<> (MainActivity.class);
    private MainActivity mActivity= null;
    @Before
    public void setUp() throws Exception {
        mActivity= mActivityTestRule.getActivity ();

    }
    @Test
    public  void  testLunchingMainAc()
    {
        View view =mActivity.findViewById (R.id.nav_view);
        assertNotNull (view);
    }

    @Test
    public void MainActivityMustLoadFragmentContainer()
    {
        View view= mActivity.findViewById (R.id.fragment_container);
        assertNotNull (view);
    }

    @Test
    public  void  LoadingFragment()
    {
      //  Espresso.onView (withId(R.id.fragment_container)).check (matches ());
      // Espresso.onView (withId(R.id.button_sign_up)).perform (click());
       Espresso.onView (withId (R.id.nav_view)).check (matches(isDisplayed ()));

    }

    @After
    public void tearDown() throws Exception {
    }
}