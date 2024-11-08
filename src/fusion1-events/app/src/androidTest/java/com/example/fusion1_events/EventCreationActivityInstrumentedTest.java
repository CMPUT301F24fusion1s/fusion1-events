package com.example.fusion1_events;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class EventCreationActivityInstrumentedTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void testEventCreation() {
        // Launch the EventCreationActivity
        ActivityScenario.launch(EventCreationActivity.class);

        // Fill in the event details
        Espresso.onView(ViewMatchers.withId(R.id.titleInput)).perform(ViewActions.typeText("Test Event"));
        Espresso.onView(ViewMatchers.withId(R.id.yearInput)).perform(ViewActions.typeText("2023"));
        Espresso.onView(ViewMatchers.withId(R.id.monthInput)).perform(ViewActions.typeText("12"));
        Espresso.onView(ViewMatchers.withId(R.id.dayInput)).perform(ViewActions.typeText("25"));
        Espresso.onView(ViewMatchers.withId(R.id.hourInput)).perform(ViewActions.typeText("10"));
        Espresso.onView(ViewMatchers.withId(R.id.minuteInput)).perform(ViewActions.typeText("30"));
        Espresso.onView(ViewMatchers.withId(R.id.locationInput)).perform(ViewActions.typeText("Test Location"));
        Espresso.onView(ViewMatchers.withId(R.id.maxWinnersInput)).perform(ViewActions.typeText("5"));
        Espresso.onView(ViewMatchers.withId(R.id.descriptionInput)).perform(ViewActions.typeText("Test Description"));
        Espresso.onView(ViewMatchers.withId(R.id.geoLocationCheckbox)).perform(ViewActions.click());

        // Close the keyboard
        Espresso.closeSoftKeyboard();

        // Click the add image button
        Espresso.onView(ViewMatchers.withId(R.id.addImageButton)).perform(ViewActions.click());

        // Verify that the image picker intent is launched
        Intents.intended(IntentMatchers.hasAction(Intent.ACTION_PICK));
        Intents.intended(IntentMatchers.hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        // Simulate selecting an image
        Uri imageUri = Uri.parse("android.resource://com.example.fusion1_events/drawable/baseline_image_64");
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK)).respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        // Click the create button
        Espresso.onView(ViewMatchers.withId(R.id.createButton)).perform(ViewActions.click());

        // Verify that the event creation was successful
        Espresso.onView(ViewMatchers.withText("Event created successfully")).inRoot(new ToastMatcher()).check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}