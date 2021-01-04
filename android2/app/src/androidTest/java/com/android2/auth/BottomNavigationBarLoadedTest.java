package com.android2.auth;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.android2.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class BottomNavigationBarLoadedTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void bottomNavigationBarLoadedTest() {
        ViewInteraction appCompatEditText = onView(withId(R.id.editText_email));
        appCompatEditText.perform(click());

        ViewInteraction appCompatEditText2 = onView(withId(R.id.editText_email));
        appCompatEditText2.perform(replaceText("phat@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(withId(R.id.editText_password));
        appCompatEditText3.perform(replaceText("test123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(withId(R.id.button_sign_in));
        appCompatButton.perform(click());

        SleepFor(2000);

        ViewInteraction frameLayout = onView(withId(R.id.navigation_map));
        frameLayout.check(matches(isDisplayed()));

        ViewInteraction frameLayout2 = onView(withId(R.id.navigation_people));
        frameLayout2.check(matches(isDisplayed()));

        ViewInteraction frameLayout3 = onView(withId(R.id.navigation_compass));
        frameLayout3.check(matches(isDisplayed()));

        ViewInteraction frameLayout4 = onView(withId(R.id.navigation_profile));
        frameLayout4.check(matches(isDisplayed()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    private void SleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
