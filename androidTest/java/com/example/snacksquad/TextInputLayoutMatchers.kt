package com.example.snacksquad

import android.view.View
import com.google.android.material.textfield.TextInputLayout
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object TextInputLayoutMatchers {
    fun hasTextInputLayoutErrorText(expectedError: String): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("TextInputLayout with error: $expectedError")
            }

            override fun matchesSafely(view: View): Boolean {
                val textInputLayout = view as? TextInputLayout ?: return false
                return textInputLayout.error?.toString() == expectedError
            }
        }
    }
}
