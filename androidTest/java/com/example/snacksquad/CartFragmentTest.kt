package com.example.snacksquad

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.ui.cart.CartFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CartFragmentTest {

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        RepositoryProvider.resetCartForTests()
        RepositoryProvider.clearOrderHistoryForTests(context)
    }

    @Test
    fun cartRecyclerViewDisplaysSixItemsByDefault() {
        launchCartFragment()

        onView(withId(R.id.cartRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(6)))
    }

    @Test
    fun clickingMinusWhenQuantityIsOneDoesNotDecreaseBelowOne() {
        launchCartFragment()

        onView(withId(R.id.cartRecyclerView)).perform(
            actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                0,
                TestViewActions.clickChildViewWithId(R.id.minusButton)
            )
        )

        onView(
            RecyclerViewMatcher.atPositionOnView(
                R.id.cartRecyclerView,
                0,
                R.id.cartItemQuantity
            )
        ).check(matches(withText("1")))
    }

    @Test
    fun clickingPlusIncreasesQuantityDisplayed() {
        launchCartFragment()

        onView(withId(R.id.cartRecyclerView)).perform(
            actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                0,
                TestViewActions.clickChildViewWithId(R.id.addButton)
            )
        )

        onView(
            RecyclerViewMatcher.atPositionOnView(
                R.id.cartRecyclerView,
                0,
                R.id.cartItemQuantity
            )
        ).check(matches(withText("2")))
    }

    @Test
    fun clickingDeleteRemovesItemAndRecyclerViewShowsFiveItems() {
        launchCartFragment()

        onView(withId(R.id.cartRecyclerView)).perform(
            actionOnItemAtPosition<androidx.recyclerview.widget.RecyclerView.ViewHolder>(
                0,
                TestViewActions.clickChildViewWithId(R.id.deleteButton)
            )
        )

        onView(withId(R.id.cartRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(5)))
    }

    @Test
    fun placeOrderClearsTheCartAndShowsEmptyState() {
        launchCartFragment()

        onView(withId(R.id.placeOrderButton)).perform(click())

        onView(withId(R.id.cartRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(0)))
        onView(withId(R.id.cartEmptyStateTextView)).check(matches(isDisplayed()))
    }

    private fun launchCartFragment() {
        launchFragmentInContainer<CartFragment>(themeResId = R.style.Theme_SnackSquad)
    }
}
