package com.example.fusion1_events;

import static androidx.test.espresso.intent.Intents.intended;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserProfileEditTest {

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    private Intent createTestIntent() {
        // Create a test user object
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

        // Set up the intent to launch MainMenuActivity with the test user data
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainMenuActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", testUser);
        intent.putExtras(bundle);

        return intent;
    }

    @Test
    public void testRegisterAndEditProfileName() throws InterruptedException {
        // Launch the MainMenuActivity with the test user
        ActivityScenario<MainMenuActivity> scenario = ActivityScenario.launch(createTestIntent());

        // Click the "Edit Profile" button to navigate to EditProfileFragment
        Espresso.onView(ViewMatchers.withId(R.id.btnProfile)).perform(ViewActions.click());

        // Wait for a short time to ensure that the fragment is loaded
        Thread.sleep(2000);

        Espresso.onView(ViewMatchers.withId(R.id.editProfileButton)).perform(ViewActions.click());

        // Verify that the Edit Profile screen is displayed
        Espresso.onView(ViewMatchers.withId(R.id.e_name)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Change the name and email to a new value
        Espresso.onView(ViewMatchers.withId(R.id.e_name)).perform(ViewActions.replaceText("Jane Doe"));

        Espresso.onView(ViewMatchers.withId(R.id.e_email)).perform(ViewActions.replaceText("changed@email.com"));

        // Close the keyboard
        Espresso.closeSoftKeyboard();

        // Click the "Save Changes" button
        Espresso.onView(ViewMatchers.withId(R.id.saveChangesButton)).perform(ViewActions.click());

        // Re-enter the Edit Profile screen by clicking "Edit Profile" again to verify the name update
        Espresso.onView(ViewMatchers.withId(R.id.btnProfile)).perform(ViewActions.click());

        // Wait for a short time to ensure that the fragment is loaded again
        Thread.sleep(2000);

        // Verify that the name and email field now shows the updated name
        Espresso.onView(ViewMatchers.withId(R.id.tvProfileName)).check(ViewAssertions.matches(ViewMatchers.withText("Jane Doe")));
        Espresso.onView(ViewMatchers.withId(R.id.tvProfileEmail)).check(ViewAssertions.matches(ViewMatchers.withText("changed@email.com")));
    }
}
