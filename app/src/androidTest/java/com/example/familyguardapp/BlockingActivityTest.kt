package com.example.familyguardapp

import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BlockingActivityTest {
    @Test
    fun checkExitButton() {
        val scenario = ActivityScenario.launch(BlockingActivity::class.java)

        onView(withId(R.id.close_button)).perform(click())

        Thread.sleep(3000)

        assertTrue(scenario.state.isAtLeast(Lifecycle.State.DESTROYED))
    }
}
