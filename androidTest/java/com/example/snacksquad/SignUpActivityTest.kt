package com.example.snacksquad

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExternalResource
import org.junit.rules.RuleChain
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpActivityTest {

    private val databaseSetupRule = object : ExternalResource() {
        override fun before() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.deleteDatabase(DATABASE_NAME)
            DBHelper(context).insertdata(EXISTING_USERNAME, EXISTING_PASSWORD)
        }

        override fun after() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.deleteDatabase(DATABASE_NAME)
        }
    }

    private val activityRule = ActivityScenarioRule(SignUpActivity::class.java)

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(databaseSetupRule).around(activityRule)

    @Test
    fun mismatchedPasswordsShowsConfirmPasswordError() {
        onView(withId(R.id.eTName)).perform(replaceText("newuser"))
        onView(withId(R.id.eTMail)).perform(replaceText("password123"))
        onView(withId(R.id.eTPass)).perform(replaceText("password321"), closeSoftKeyboard())

        onView(withId(R.id.signUpConfirmPasswordInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText("Passwords do not match.")
            )
        )
        onView(withId(R.id.btnSignUp)).check(matches(not(isEnabled())))
    }

    @Test
    fun duplicateUsernameShowsError() {
        onView(withId(R.id.eTName)).perform(replaceText(EXISTING_USERNAME))
        onView(withId(R.id.eTMail)).perform(replaceText(EXISTING_PASSWORD))
        onView(withId(R.id.eTPass)).perform(replaceText(EXISTING_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.btnSignUp)).perform(click())

        onView(withId(R.id.signUpUsernameInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText("Username already exists.")
            )
        )
    }

    @Test
    fun validSignUpNavigatesToLoginActivity() {
        onView(withId(R.id.eTName)).perform(replaceText("freshuser"))
        onView(withId(R.id.eTMail)).perform(replaceText("password123"))
        onView(withId(R.id.eTPass)).perform(replaceText("password123"), closeSoftKeyboard())
        onView(withId(R.id.btnSignUp)).perform(click())

        onView(withId(R.id.bTLogin)).check(matches(isDisplayed()))
    }

    companion object {
        private const val DATABASE_NAME = "Userdata"
        private const val EXISTING_USERNAME = "existinguser"
        private const val EXISTING_PASSWORD = "password123"
    }
}
