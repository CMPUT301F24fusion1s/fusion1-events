package com.example.fusion1_events;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.example.fusion1_events.MainActivity;

/**
 * UI test that starts from the login screen, clicks the register button,
 * goes to the profile screen, edits the profile, and verifies the edited fields.
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileEditTest {

    // Launch the MainActivity at the start of the test
    @Rule
    public ActivityScenarioRule<MainActivity> activityTestRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void testUserRegistrationAndProfileEdit() {


        // Click on the Register button
        onView(withId(R.id.registerButton))
                .perform(ViewActions.click());

        // Wait for the registration screen to appear
        onView(withId(R.id.et_name))
                .check(matches(isDisplayed()));

        // Fill in registration details
        onView(withId(R.id.et_name)).perform(ViewActions.typeText("Test User"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_email)).perform(ViewActions.typeText("testuser@example.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.et_phone_number)).perform(ViewActions.typeText("1234567890"), ViewActions.closeSoftKeyboard());

        // Click on the Register button to complete registration
        onView(withId(R.id.btn_register))
                .perform(ViewActions.click());

        // Wait for the MainMenuActivity to appear
        onView(withId(R.id.btnProfile))
                .check(matches(isDisplayed()));

        // Click on the Profile button to go to the UserProfileFragment
        onView(withId(R.id.btnProfile))
                .perform(ViewActions.click());

        // Wait for the UserProfileFragment to appear
        onView(withId(R.id.tvProfileName))
                .check(matches(isDisplayed()));

        // Click on Edit Profile button
        onView(withId(R.id.editProfileButton))
                .perform(ViewActions.click());

        // Wait for the EditProfileFragment to appear
        onView(withId(R.id.e_name))
                .check(matches(isDisplayed()));

        // Edit the profile details
        onView(withId(R.id.e_name)).perform(ViewActions.clearText(), ViewActions.typeText("Updated User"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.e_email)).perform(ViewActions.clearText(), ViewActions.typeText("updateduser@example.com"), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.e_phone)).perform(ViewActions.clearText(), ViewActions.typeText("0987654321"), ViewActions.closeSoftKeyboard());

        // Save the edited profile
        onView(withId(R.id.saveChangesButton))
                .perform(ViewActions.click());

        // Verify that the profile details have been updated
        onView(withId(R.id.tvProfileName)).check(matches(withText("Updated User")));
        onView(withId(R.id.tvProfileEmail)).check(matches(withText("updateduser@example.com")));
        onView(withId(R.id.tvProfilePhoneNumber)).check(matches(withText("0987654321")));
    }
}
