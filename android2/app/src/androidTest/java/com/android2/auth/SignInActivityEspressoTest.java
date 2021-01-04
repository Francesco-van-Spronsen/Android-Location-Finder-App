package com.android2.auth;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import com.android2.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignInActivityEspressoTest {

    @Rule
    public ActivityTestRule<SignInActivity> mActivityTestRule = new ActivityTestRule<>(SignInActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void signInActivityTest() {
        ViewInteraction emailEditText = onView(allOf(withId(R.id.editText_email), isDisplayed()));
        SleepFor(1000);
        emailEditText.perform(replaceText("test@gmail.co"), closeSoftKeyboard());

        ViewInteraction passwordEditText = onView(allOf(withId(R.id.editText_password), isDisplayed()));
        SleepFor(1000);
        passwordEditText.perform(replaceText("123456"), closeSoftKeyboard());

        SleepFor(1000);
        ViewInteraction signInButton = onView(allOf(withId(R.id.button_sign_in), withText("Sign in"), isDisplayed()));
        signInButton.perform(click());
        SleepFor(2000);

        onView(withId(R.id.textView_message)).check(matches(withText("Invalid email or password.")));

        SleepFor(1000);
        emailEditText.perform(replaceText("test@gmail.com"), closeSoftKeyboard());

        SleepFor(1000);
        passwordEditText.perform(replaceText("123456"), closeSoftKeyboard());
        SleepFor(1000);

        signInButton.perform(click());
        SleepFor(2000);
    }

    private void SleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
