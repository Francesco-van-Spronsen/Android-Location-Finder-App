package com.android2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewAssertion;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.NavigationViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.android2.auth.SignInActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.internal.matchers.Null;

import java.util.regex.Pattern;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withInputType;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;
@RunWith (AndroidJUnit4.class)
@LargeTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EpressoMainActivityTest {
 @Rule
 public ActivityTestRule<SignInActivity>mActivityTestRule =new ActivityTestRule<> (SignInActivity.class);
 private SignInActivity sActivity=null;
    @Before
    public void setUp() throws Exception {
        sActivity = mActivityTestRule.getActivity ();
    }
     // first test
    @Test
    public  void  GuiMustShowSignInMessage()
    {
        Espresso.onView (withId (R.id.textView_sign_in)).check (matches(isDisplayed ()));
    }
    @Test
    public  void  GuiMustShowSignUpButton()
    {
        Espresso.onView (withId (R.id.button_sign_up)).check (matches(isDisplayed ()));
    }
    @Test
    public  void  GuiMustShowSignInButton()
    {
        Espresso.onView (withId (R.id.button_sign_in)).check (matches(isDisplayed ()));
    }
    @Test
    public  void  GuiMustShowEmailTextField()
    {
        Espresso.onView (withId (R.id.editText_email)).check (matches(isDisplayed ()));
    }

    @Test
    public  void  LoginScenario()
    {
        Espresso.onView (withId (R.id.editText_email)).perform(typeText("dingagang@yahoo.com"));
       // Espresso.closeSoftKeyboard ();
        Espresso.onView (withId (R.id.editText_password)).perform(typeText("android]2"));
        Espresso.closeSoftKeyboard ();
        Espresso.onView (withId (R.id.button_sign_in)).perform (click());

    }

    @Test
    public  void  SignUpScenario()
    {
        Espresso.onView (withId (R.id.button_sign_up)).perform (click());
        Espresso.onView (withId (R.id.editText_email)).perform(typeText("d.gang@student.fontys.nl"));
        // Espresso.closeSoftKeyboard ();
        Espresso.onView (withId (R.id.editText_password)).perform(typeText("android]3"));
         Espresso.closeSoftKeyboard ();
        Espresso.onView (withId (R.id.editText_confirm_password)).perform(typeText("android]3"));
        Espresso.closeSoftKeyboard ();
        Espresso.onView (withId (R.id.button_sign_up)).perform (click());

    }

    @After
    public void tearDown() throws Exception {
     //   sActivity= null;
    }
}