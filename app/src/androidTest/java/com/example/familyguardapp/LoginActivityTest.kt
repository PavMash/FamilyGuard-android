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
class LoginActivityTest {
	@Test
	fun checkValidLogin() {
		ActivityScenario.launch(SignUpActivity::class.java)

		onView(withId(R.id.edSignEmail)).perform(typeText("123@gmail.com"), closeSoftKeyboard())
		onView(withId(R.id.edSignPassword)).perform(typeText("123456"), closeSoftKeyboard())

		onView(withId(R.id.btnSignSignUp)).perform(click())

		ActivityScenario.launch(LoginActivity::class.java)

		onView(withId(R.id.edEmail)).perform(typeText("123@gmail.com"), closeSoftKeyboard())
		onView(withId(R.id.edPassword)).perform(typeText("123456"), closeSoftKeyboard())

		onView(withId(R.id.btnLogIn)).perform(click())

		Thread.sleep(3000)
		onView(withId(R.id.permission_instruction)).check(matches(isDisplayed()))
	}

	@Test
	fun checkInvalidLogin() {
		ActivityScenario.launch(SignUpActivity::class.java)

		onView(withId(R.id.edSignEmail)).perform(typeText("user@gmail.com"), closeSoftKeyboard())
		onView(withId(R.id.edSignPassword)).perform(typeText("qwerty"), closeSoftKeyboard())

		onView(withId(R.id.btnSignSignUp)).perform(click())

		ActivityScenario.launch(LoginActivity::class.java)

		onView(withId(R.id.edEmail)).perform(typeText(""), closeSoftKeyboard())
		onView(withId(R.id.edPassword)).perform(typeText(""), closeSoftKeyboard())

		onView(withId(R.id.btnLogIn)).perform(click())

		onView(withId(R.id.permission_instruction)).check(doesNotExist())
	}
}