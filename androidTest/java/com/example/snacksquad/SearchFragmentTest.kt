package com.example.snacksquad

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.snacksquad.data.repository.RepositoryProvider
import com.example.snacksquad.ui.search.SearchFragment
import com.example.snacksquad.ui.search.SearchViewModel
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        RepositoryProvider.resetCartForTests()
        RepositoryProvider.clearOrderHistoryForTests(context)
        SearchViewModel.debounceDurationMillis = 0L
    }

    @After
    fun tearDown() {
        SearchViewModel.debounceDurationMillis = 300L
    }

    @Test
    fun typingBurgerShowsExactlyOneResult() {
        launchSearchFragment()

        onView(withId(R.id.searchBar)).perform(click())
        onView(withId(R.id.searchViewEditText)).perform(replaceText("Burger"), closeSoftKeyboard())

        onView(withId(R.id.searchResultsRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(1)))
    }

    @Test
    fun clearingSearchBarShowsAllItems() {
        launchSearchFragment()

        onView(withId(R.id.searchBar)).perform(click())
        onView(withId(R.id.searchViewEditText)).perform(replaceText("Burger"), closeSoftKeyboard())
        onView(withId(R.id.searchViewEditText)).perform(replaceText(""), closeSoftKeyboard())

        onView(withId(R.id.searchResultsRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(6)))
    }

    @Test
    fun typingNonExistentItemShowsZeroResultsAndEmptyState() {
        launchSearchFragment()

        onView(withId(R.id.searchBar)).perform(click())
        onView(withId(R.id.searchViewEditText)).perform(replaceText("Sushi"), closeSoftKeyboard())
        onView(withId(R.id.searchResultsRecyclerView)).check(matches(RecyclerViewMatcher.hasItemCount(0)))
        pressBack()

        onView(withId(R.id.searchEmptyStateTextView)).check(matches(isDisplayed()))
    }

    private fun launchSearchFragment() {
        launchFragmentInContainer<SearchFragment>(themeResId = R.style.Theme_SnackSquad)
    }
}
