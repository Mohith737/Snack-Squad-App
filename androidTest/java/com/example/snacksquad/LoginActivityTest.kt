package com.example.snacksquad

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.clearText
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
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
class LoginActivityTest {

    private val databaseSetupRule = object : ExternalResource() {
        override fun before() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.deleteDatabase(DATABASE_NAME)
            DBHelper(context).insertdata(TEST_USERNAME, TEST_PASSWORD)
        }

        override fun after() {
            val context = InstrumentationRegistry.getInstrumentation().targetContext
            context.deleteDatabase(DATABASE_NAME)
        }
    }

    private val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @get:Rule
    val ruleChain: RuleChain = RuleChain.outerRule(databaseSetupRule).around(activityRule)

    @Test
    fun emptyUsernameAndPasswordShowsErrorAndDoesNotNavigate() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(typeText("abc"), clearText())
        onView(withId(R.id.editTextTextPassword)).perform(
            typeText("abcdef"),
            clearText(),
            closeSoftKeyboard()
        )

        onView(withId(R.id.loginUsernameInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText("Username is required.")
            )
        )
        onView(withId(R.id.loginPasswordInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText("Password is required.")
            )
        )
        onView(withId(R.id.bTLogin)).check(matches(not(isEnabled())))
        onView(withId(R.id.bottomNavigationView)).check(doesNotExist())
    }

    @Test
    fun invalidUsernameFormatShowsValidationError() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText("user!"), closeSoftKeyboard())

        onView(withId(R.id.loginUsernameInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText(
                    "Username must contain only letters and numbers."
                )
            )
        )
        onView(withId(R.id.bTLogin)).check(matches(not(isEnabled())))
    }

    @Test
    fun shortPasswordShowsValidationError() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText(TEST_USERNAME))
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("123"), closeSoftKeyboard())

        onView(withId(R.id.loginPasswordInputLayout)).check(
            matches(
                TextInputLayoutMatchers.hasTextInputLayoutErrorText(
                    "Password must be 6 to 50 characters long."
                )
            )
        )
        onView(withId(R.id.bTLogin)).check(matches(not(isEnabled())))
    }

    @Test
    fun validCredentialsShowsSuccessToastAndNavigatesToMainActivity() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText(TEST_USERNAME))
        onView(withId(R.id.editTextTextPassword)).perform(replaceText(TEST_PASSWORD), closeSoftKeyboard())
        onView(withId(R.id.bTLogin)).perform(click())

        onView(withText("Login successful"))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
        onView(withId(R.id.bottomNavigationView)).check(matches(isDisplayed()))
    }

    @Test
    fun wrongPasswordShowsErrorToastAndStaysOnLoginActivity() {
        onView(withId(R.id.editTextTextEmailAddress)).perform(replaceText(TEST_USERNAME))
        onView(withId(R.id.editTextTextPassword)).perform(replaceText("wrongpass"), closeSoftKeyboard())
        onView(withId(R.id.bTLogin)).perform(click())

        onView(withText("Invalid username or password."))
            .inRoot(ToastMatcher())
            .check(matches(isDisplayed()))
        onView(withId(R.id.bTLogin)).check(matches(isDisplayed()))
        onView(withId(R.id.bottomNavigationView)).check(doesNotExist())
    }

    companion object {
        private const val DATABASE_NAME = "Userdata"
        private const val TEST_USERNAME = "validuser"
        private const val TEST_PASSWORD = "password123"
    }
}
