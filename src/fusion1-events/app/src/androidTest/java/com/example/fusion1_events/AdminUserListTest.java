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

import com.example.fusion1_events.AdminMainMenuActivity;
import com.example.fusion1_events.R;

@RunWith(AndroidJUnit4.class)
public class AdminUserListTest {

    @Rule
    public ActivityScenarioRule<AdminMainMenuActivity> activityRule =
            new ActivityScenarioRule<>(AdminMainMenuActivity.class);

    @Test
    public void testUserListUI() {
        // Click on "View Profiles" button
        onView(withId(R.id.btn_view_profiles)).perform(click());

        // Verify that the profile list layout is displayed
        onView(withId(R.id.profile_list_layout)).check(matches(isDisplayed()));

        // Check if the header text "List of Profiles" is displayed
        onView(withId(R.id.profile_header)).check(matches(withText("List of Profiles")));

//        // Verify that clicking the back button navigates to the main menu
//        onView(withId(R.id.backArrowUsers)).perform(click());
//        onView(withId(R.id.btn_view_profiles)).check(matches(isDisplayed()));
    }

    @Test
    public void testDeleteUserProfile() {
        // Navigate to the user list
        onView(withId(R.id.btn_view_profiles)).perform(click());

        // Simulate clicking the delete button on the first user profile (e.g., RecyclerView item)
        onView(withId(R.id.delete_button)).perform(click());

        // Verify that the confirmation dialog is displayed
        onView(withText("Are you sure you want to delete this user?")).check(matches(isDisplayed()));

        // Confirm the deletion
        onView(withText("Yes")).perform(click());
    }
}
