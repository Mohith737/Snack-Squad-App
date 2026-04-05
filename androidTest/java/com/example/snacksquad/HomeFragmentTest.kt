package com.example.snacksquad

import androidx.fragment.app.testing.launchInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.snacksquad.ui.home.HomeFragment
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeFragmentTest {

    @Test
    fun popularItemsRecyclerViewIsDisplayedAndHasAtLeastFourItems() {
        launchInContainer<HomeFragment>(themeResId = R.style.Theme_SnackSquad)

        onView(withId(R.id.PopulerRecyclerView)).check(matches(isDisplayed()))
        onView(withId(R.id.PopulerRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(6)))
    }

    @Test
    fun imageSliderIsVisible() {
        launchInContainer<HomeFragment>(themeResId = R.style.Theme_SnackSquad)

        onView(withId(R.id.image_slider)).check(matches(isDisplayed()))
    }
}
