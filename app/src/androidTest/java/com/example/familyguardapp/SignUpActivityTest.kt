package com.example.familyguardapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {
	@Test
	fun checkValidSignUp() {
		ActivityScenario.launch(SignUpActivity::class.java)

		val randomEmail = "user"
			.plus((1..1_000_000).random())
			.plus((1..1_000_000).random())
			.plus(System.currentTimeMillis())
			.plus("@gmail.com")

		onView(withId(R.id.edSignEmail)).perform(typeText(randomEmail), closeSoftKeyboard())
		onView(withId(R.id.edSignPassword)).perform(typeText("password"), closeSoftKeyboard())

		onView(withId(R.id.btnSignSignUp)).perform(click())

		Thread.sleep(3000)
		onView(withId(R.id.btnLogIn)).check(matches(isDisplayed()))
	}

	@Test
	fun checkInvalidSignUp() {
		ActivityScenario.launch(SignUpActivity::class.java)

		onView(withId(R.id.edSignEmail)).perform(typeText("invalidgmailcom"), closeSoftKeyboard())
		onView(withId(R.id.edSignPassword)).perform(typeText("invalid"), closeSoftKeyboard())

		onView(withId(R.id.btnSignSignUp)).perform(click())

		onView(withId(R.id.tvTitle)).check(doesNotExist())
	}
}