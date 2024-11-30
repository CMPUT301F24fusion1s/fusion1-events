package com.example.fusion1_events;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class AdminUITest {

    @Rule
    public ActivityScenarioRule<AdminMainMenuActivity> activityRule =
            new ActivityScenarioRule<>(AdminMainMenuActivity.class);

    @Test
    public void testViewUsersAndDeleteUser() {
        // Step 1: Check that the app opens to the main menu
        onView(withId(R.id.btn_view_profiles)).check(matches(isDisplayed()));

        // Step 2: Press the "View Users" button
        onView(withId(R.id.btn_view_profiles)).perform(click());

        // Step 3: Verify that the user list screen is displayed
        onView(withId(R.id.profile_list_layout)).check(matches(isDisplayed()));

        // Step 4: Delete a user
        onView(withId(R.id.delete_button)) // Find the delete button for a user
                .perform(click());

        // Optional: Check that the user was removed from the list
        // Adjust this if you have a specific ID or mechanism to verify deletion
        onView(withText("Are you sure you want to delete this user?")) // Example confirmation text
                .check(matches(isDisplayed()));
    }
}
