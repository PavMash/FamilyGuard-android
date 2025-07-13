package com.example.familyguardapp

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Before
    fun getFCMTokenFromLoginPage() {
        val loginScenario = ActivityScenario.launch(LoginActivity::class.java)

        Thread.sleep(5000)

        loginScenario.close()
    }

    @Test
    fun checkValidLogin() {
        ActivityScenario.launch(SignUpActivity::class.java)

        val randomEmail = "user"
            .plus((1..1_000_000).random())
            .plus((1..1_000_000).random())
            .plus(System.currentTimeMillis())
            .plus("@gmail.com")

        onView(withId(R.id.edSignEmail)).perform(typeText(randomEmail), closeSoftKeyboard())
        onView(withId(R.id.edSignPassword)).perform(typeText("123456"), closeSoftKeyboard())

        onView(withId(R.id.btnSignSignUp)).perform(click())
        onView(withId(R.id.btnSignSignUp)).perform(click())

        Thread.sleep(10000)

        onView(withId(R.id.edEmail)).perform(typeText(randomEmail), closeSoftKeyboard())
        onView(withId(R.id.edPassword)).perform(typeText("123456"), closeSoftKeyboard())

        onView(withId(R.id.btnLogIn)).perform(click())

        Thread.sleep(5000)

        val childName = "Magomed"

        onView(withId(R.id.edChildName)).perform(typeText(childName), closeSoftKeyboard())

        onView(withId(R.id.btnAddChild)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.next_button)).perform(scrollTo()).perform(click())

        Thread.sleep(5000)

        val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        val isAdminDialogVisible = device.wait(Until.hasObject(By.textContains("Activate")), 5000)

        assertTrue("Device Admin dialog should appear after login", isAdminDialogVisible)
    }

    @Test
    fun checkInvalidLogin() {
        ActivityScenario.launch(SignUpActivity::class.java)

        val randomEmail = "user"
            .plus((1..1_000_000).random())
            .plus((1..1_000_000).random())
            .plus(System.currentTimeMillis())
            .plus("@gmail.com")

        onView(withId(R.id.edSignEmail)).perform(typeText(randomEmail), closeSoftKeyboard())
        onView(withId(R.id.edSignPassword)).perform(typeText("qwerty"), closeSoftKeyboard())

        onView(withId(R.id.btnSignSignUp)).perform(click())
        onView(withId(R.id.btnSignSignUp)).perform(click())

        Thread.sleep(10000)

        onView(withId(R.id.edEmail)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.edPassword)).perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.btnLogIn)).perform(click())

        Thread.sleep(5000)

        onView(withId(R.id.edChildName)).check(doesNotExist())
    }

    @Test
    fun checkEmptyCredentialsLogin() {
        ActivityScenario.launch(SignUpActivity::class.java)

        onView(withId(R.id.edSignEmail)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.edSignPassword)).perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.btnSignSignUp)).perform(click())

        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.edEmail)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.edPassword)).perform(typeText(""), closeSoftKeyboard())

        onView(withId(R.id.btnLogIn)).perform(click())

        Thread.sleep(10000)

        onView(withId(R.id.edChildName)).check(doesNotExist())
    }

    @Test
    fun checkInvalidUsername() {
        ActivityScenario.launch(SignUpActivity::class.java)

        val randomEmail = "user"
            .plus((1..1_000_000).random())
            .plus((1..1_000_000).random())
            .plus(System.currentTimeMillis())
            .plus("@gmail.com")

        onView(withId(R.id.edSignEmail)).perform(typeText(randomEmail), closeSoftKeyboard())
        onView(withId(R.id.edSignPassword)).perform(typeText("777777"), closeSoftKeyboard())

        onView(withId(R.id.btnSignSignUp)).perform(click())
        onView(withId(R.id.btnSignSignUp)).perform(click())

        Thread.sleep(10000)

        val invalidEmail = "1".plus(randomEmail)

        onView(withId(R.id.edEmail)).perform(typeText(invalidEmail), closeSoftKeyboard())
        onView(withId(R.id.edPassword)).perform(typeText("7777777"), closeSoftKeyboard())

        onView(withId(R.id.btnLogIn)).perform(click())

        Thread.sleep(10000)

        onView(withId(R.id.edChildName)).check(doesNotExist())
    }
}
