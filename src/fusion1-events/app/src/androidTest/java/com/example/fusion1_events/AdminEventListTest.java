package com.example.fusion1_events;

import androidx.test.espresso.action.ViewActions;
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

import com.example.fusion1_events.AdminMainMenuActivity;
import com.example.fusion1_events.R;

@RunWith(AndroidJUnit4.class)
public class AdminEventListTest {

    @Rule
    public ActivityScenarioRule<AdminMainMenuActivity> activityRule =
            new ActivityScenarioRule<>(AdminMainMenuActivity.class);

    @Test
    public void testEventListUI() {
        // Click on "View Events" button
        onView(withId(R.id.btn_view_event)).perform(click());

        // Verify that the event list layout is displayed
        onView(withId(R.id.event_list)).check(matches(isDisplayed()));

        // Verify that clicking the back button navigates to the main menu
        onView(withId(R.id.backArrowEvent)).perform(click());
        onView(withId(R.id.btn_view_event)).check(matches(isDisplayed()));
    }

    @Test
    public void testSelectEvent() {
        // Navigate to the event list
        onView(withId(R.id.btn_view_event)).perform(click());

        // Simulate clicking on the first event in the RecyclerView
//        onView(withId(R.id.event_list))
//                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));


        // Verify that the event details page is displayed
        onView(withId(R.id.eventTitle)).check(matches(isDisplayed()));

        // Verify that the back button works
        onView(withId(R.id.backArrowEvent)).perform(click());
        onView(withId(R.id.event_list)).check(matches(isDisplayed()));
    }
}
