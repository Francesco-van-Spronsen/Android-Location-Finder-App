package com.android2;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.android2.auth.SignInActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProfileFragmentEspressoTest {
    private String validTestLoginEmail = "dingagang@yahoo.com";
    private String validTestPassword = "android]2";

    private static boolean init = false;

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);
    private SignInActivity sActivity = null;

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Before //Log in and go to profile fragment
    public void init() {
        if (!init)
        sActivity = mActivityTestRule.getActivity();
        Espresso.onView(withId(R.id.editText_email)).perform(typeText(validTestLoginEmail));
        Espresso.closeSoftKeyboard();
        SleepFor(1000);
        Espresso.onView(withId(R.id.editText_password)).perform(typeText(validTestPassword));
        Espresso.closeSoftKeyboard();
        SleepFor(1000);
        Espresso.onView(withId(R.id.button_sign_in)).perform(click());
        SleepFor(5000);
        Espresso.onView (withId (R.id.navigation_profile)).perform (click());
        SleepFor(3000);
        init = true;
    }

    //1
    @Test
    public void changeEmailButton_ShouldBeShown() {
        ViewInteraction frameLayout = onView(withId(R.id.btnChangeEmail));
        frameLayout.check(matches(isDisplayed()));
    }

    //2
    @Test
    public void changePasswordButton_ShouldBeShown() {
        ViewInteraction frameLayout = onView(withId(R.id.btnChangePassword));
        frameLayout.check(matches(isDisplayed()));
    }

    //3
    @Test
    public void textEmail_ShouldShowUserEmail() {
        onView(withId(R.id.textEmail)).check(matches(withText(validTestPassword)));
    }

    //4
    @Test
    public void changeEmailButton_OnClick_TextShouldChange() {
        //From initial "Change Email" to "Update"
        onView(withId(R.id.btnChangeEmail)).perform(click());
        onView(withId(R.id.btnChangeEmail)).check(matches(withText("Update")));
        SleepFor(1000);

        //Now from "Update" back to "Change Email"
        onView(withId(R.id.btnChangeEmail)).perform(click());
        onView(withId(R.id.btnChangeEmail)).check(matches(withText("Change Email")));
    }

    //5
    @Test
    public void changeEmailButton_OnClick_cancelEmailButtonShouldBeShown() {
        onView(withId(R.id.btnChangeEmail)).perform(click());
        ViewInteraction frameLayout = onView(withId(R.id.btnCancelEmail));
        frameLayout.check(matches(isDisplayed()));
    }

    //6
    @Test
    public void changePasswordButton_OnClick_TextShouldChange() {
        //From initial "Change Password" to "Update"
        onView(withId(R.id.btnChangePassword)).perform(click());
        onView(withId(R.id.btnChangePassword)).check(matches(withText("Update")));
        SleepFor(1000);

        //Now from "Update" back to "Change Password"
        onView(withId(R.id.btnChangePassword)).perform(click());
        onView(withId(R.id.btnChangePassword)).check(matches(withText("Change Password")));
    }

    //7
    @Test
    public void changePasswordButton_OnClick_cancelPasswordButtonShouldBeShown() {
        onView(withId(R.id.btnChangePassword)).perform(click());
        SleepFor(1000);
        ViewInteraction frameLayout = onView(withId(R.id.btnCancelPassword));
        frameLayout.check(matches(isDisplayed()));
    }

    private void SleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
