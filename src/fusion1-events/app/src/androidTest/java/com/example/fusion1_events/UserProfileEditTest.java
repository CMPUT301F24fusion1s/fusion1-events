package com.example.fusion1_events;

import static androidx.test.espresso.intent.Intents.intended;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.WindowManager;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.matcher.IntentMatchers;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

@RunWith(AndroidJUnit4.class)
public class UserProfileEditTest {

    @Rule
    //public GrantPermissionRule permissionRule = GrantPermissionRule.grant(android.Manifest.permission.READ_EXTERNAL_STORAGE);

    private Intent createTestIntent() {
        // Create a test user
        Entrant testUser = new Entrant(
                "test@email.com",
                "Test User",
                "Entrant",
                "1234567890",
                UUID.randomUUID().toString(),
                "test_device_id",
                null,
                null,
                true
        );

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), EventCreationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable("user", testUser);
        intent.putExtras(bundle);

        return intent;
    }

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
        // Launch the EventCreationActivity with the necessary intent
        ActivityScenario<MainMenuActivity> scenario =
                ActivityScenario.launch(createTestIntent());

        // Fill in the event details
        Espresso.onView(ViewMatchers.withId(R.id.titleInput))
                .perform(ViewActions.typeText("Test Event"));
        Espresso.onView(ViewMatchers.withId(R.id.yearInput))
                .perform(ViewActions.typeText("2023"));
        Espresso.onView(ViewMatchers.withId(R.id.monthInput))
                .perform(ViewActions.typeText("12"));
        Espresso.onView(ViewMatchers.withId(R.id.dayInput))
                .perform(ViewActions.typeText("25"));
        Espresso.onView(ViewMatchers.withId(R.id.hourInput))
                .perform(ViewActions.typeText("10"));
        Espresso.onView(ViewMatchers.withId(R.id.minuteInput))
                .perform(ViewActions.typeText("30"));
        Espresso.onView(ViewMatchers.withId(R.id.locationInput))
                .perform(ViewActions.typeText("Test Location"));
        Espresso.onView(ViewMatchers.withId(R.id.maxWinnersInput))
                .perform(ViewActions.typeText("5"));
        Espresso.onView(ViewMatchers.withId(R.id.descriptionInput))
                .perform(ViewActions.typeText("Test Description"));

        // Close the keyboard
        Espresso.closeSoftKeyboard();

        Espresso.onView(ViewMatchers.withId(R.id.geoLocationCheckbox))
                .perform(ViewActions.click());

        // Click the add image button
        Espresso.onView(ViewMatchers.withId(R.id.addImageButton))
                .perform(ViewActions.click());

        // Verify that the image picker intent is launched
        intended(IntentMatchers.hasAction(Intent.ACTION_PICK));
        intended(IntentMatchers.hasData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI));

        // Simulate selecting an image
        Uri imageUri = Uri.parse("android.resource://com.example.fusion1_events/drawable/baseline_image_64");
        Intent resultData = new Intent();
        resultData.setData(imageUri);
        Intents.intending(IntentMatchers.hasAction(Intent.ACTION_PICK))
                .respondWith(new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData));

        // Click the create button
        Espresso.onView(ViewMatchers.withId(R.id.createButton))
                .perform(ViewActions.click());

        // Verify that the event creation was successful
        Espresso.onView(ViewMatchers.withText("Event created successfully"))
                .inRoot(new ToastMatcher())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        // Verify navigation to EventsPageActivity
        intended(IntentMatchers.hasComponent(EventsPageActivity.class.getName()));
    }

    // Add the ToastMatcher class if not already defined
    public class ToastMatcher extends TypeSafeMatcher<Root> {
        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                return windowToken == appToken;
            }
            return false;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("is toast");
        }
    }
}
