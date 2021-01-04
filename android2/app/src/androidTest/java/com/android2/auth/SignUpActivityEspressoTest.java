package com.android2.auth;

import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;
import com.android2.R;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.Random;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SignUpActivityEspressoTest {

    @Rule
    public ActivityTestRule<SignUpActivity> mActivityTestRule = new ActivityTestRule<>(SignUpActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.ACCESS_COARSE_LOCATION");

    @Test
    public void signUpActivityEspressoTest() {
        String email = "user" + randomInt() + "@example.com";
        String invalidEmail = "user" + randomInt() + "@example.c";
        String password = "password" + randomInt();
        String confirmPassword = password + "confirm";

        ViewInteraction buttonSignUp = onView(allOf(withId(R.id.button_sign_up), isDisplayed()));
        buttonSignUp.perform(click());

        SleepFor(1000);
        ViewInteraction editTextEmail = onView(allOf(withId(R.id.editText_email), isDisplayed()));
        editTextEmail.perform(replaceText(invalidEmail), closeSoftKeyboard());

        SleepFor(1000);
        ViewInteraction editTextPassword = onView(allOf(withId(R.id.editText_password), isDisplayed()));
        editTextPassword.perform(replaceText(password), closeSoftKeyboard());

        SleepFor(1000);
        ViewInteraction editTextConfirmPassword = onView(allOf(withId(R.id.editText_confirm_password), isDisplayed()));
        editTextConfirmPassword.perform(replaceText(confirmPassword), closeSoftKeyboard());

        SleepFor(1000);
        buttonSignUp.perform(click());
        SleepFor(2000);
        onView(withId(R.id.textView_message)).check(matches(withText("Passwords do not match.")));

        SleepFor(1000);
        editTextConfirmPassword.perform(replaceText(password), closeSoftKeyboard());

        buttonSignUp.perform(click());
        SleepFor(2000);
        onView(withId(R.id.textView_message)).check(matches(withText("The email address is badly formatted.")));

        SleepFor(1000);
        editTextEmail.perform(replaceText(email), closeSoftKeyboard());

        SleepFor(1000);
        editTextPassword.perform(replaceText(password), closeSoftKeyboard());

        SleepFor(1000);
        editTextConfirmPassword.perform(replaceText(password), closeSoftKeyboard());

        SleepFor(1000);
        buttonSignUp.perform(click());
        SleepFor(2000);
    }

    private String randomInt() {
        return String.valueOf(((new Random()).nextInt(100000)));
    }

    private void SleepFor(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
