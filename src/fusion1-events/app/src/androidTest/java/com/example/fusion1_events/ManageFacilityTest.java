package com.example.fusion1_events;

import static androidx.test.espresso.intent.Intents.intended;

import android.content.Intent;
import android.os.Bundle;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ManageFacilityTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private Intent createTestIntent() {
        // Create a test user object and set up the intent to launch EventsPageActivity
        Entrant testUser = new Entrant(
                "test@email.com",
                "John Doe",
                "Entrant",
                "1234567890",
                UUID.randomUUID().toString(),
                "test_device_id",
                null,
                null,
                true
        );

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventsPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", testUser);
        intent.putExtras(bundle);

        return intent;
    }

    @Test
    public void testAddAndEditFacility() throws InterruptedException {
        // Launch the EventsPageActivity with the test user
        ActivityScenario<EventsPageActivity> scenario = ActivityScenario.launch(createTestIntent());

        // Click on "Manage Facility" to navigate to FacilityPageActivity
        Espresso.onView(ViewMatchers.withId(R.id.tvManageFacility)).perform(ViewActions.click());

        // Verify navigation to FacilityPageActivity
        intended(IntentMatchers.hasComponent(FacilityPageActivity.class.getName()));

        // Wait for a short time to ensure that the UI of FacilityPageActivity is loaded
        Thread.sleep(1000);

        // Click on the "Add Facility" button to navigate to FacilityAddActivity
        Espresso.onView(ViewMatchers.withId(R.id.fabAddFacility)).perform(ViewActions.click());

        // Verify that the FacilityAddActivity is started
        //intended(IntentMatchers.hasComponent(FacilityAddActivity.class.getName()));

        // Wait for a short time to ensure that the FacilityAddActivity UI is loaded
        Thread.sleep(1000);

        // Fill in the facility name and location
        Espresso.onView(ViewMatchers.withId(R.id.NameInput)).perform(ViewActions.replaceText("Test Facility"));
        Espresso.onView(ViewMatchers.withId(R.id.etFacilityLocation)).perform(ViewActions.replaceText("Test Location"));

        // Close the keyboard
        Espresso.closeSoftKeyboard();

        // Click the "Add" button to add the facility
        Espresso.onView(ViewMatchers.withId(R.id.btnAdd)).perform(ViewActions.click());

        // Verify that the facility was successfully added and returned to FacilityPageActivity
        //intended(IntentMatchers.hasComponent(FacilityPageActivity.class.getName()));

        // Wait for a short time to ensure that the FacilityPageActivity UI is loaded again
        Thread.sleep(1000);

        // Select the added facility ("Test Facility") to edit
        Espresso.onView(ViewMatchers.withId(R.id.btnEdit)).perform(ViewActions.click());


        // Wait for a short time to ensure that the FacilityEditActivity UI is loaded
        Thread.sleep(1000);

        // Edit the facility name and location
        Espresso.onView(ViewMatchers.withId(R.id.etFacilityName)).perform(ViewActions.replaceText("Updated Facility Name"));
        Espresso.onView(ViewMatchers.withId(R.id.etFacilityLocation)).perform(ViewActions.replaceText("Updated Location"));

        // Close the keyboard
        Espresso.closeSoftKeyboard();

        // Click the "Save" button to save the changes
        Espresso.onView(ViewMatchers.withId(R.id.btnSave)).perform(ViewActions.click());

        // Verify that the facility was successfully updated and returned to FacilityPageActivity
        //intended(IntentMatchers.hasComponent(FacilityPageActivity.class.getName()));

        // Verify that the updated facility name is displayed
        Espresso.onView(ViewMatchers.withText("Updated Facility Name")).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
