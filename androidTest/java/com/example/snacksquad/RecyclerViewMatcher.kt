package com.example.snacksquad

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher

object RecyclerViewMatcher {
    fun hasItemCount(expectedCount: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView with item count: $expectedCount")
            }

            override fun matchesSafely(view: View): Boolean {
                return (view as? RecyclerView)?.adapter?.itemCount == expectedCount
            }
        }
    }

    fun atPositionOnView(
        recyclerViewId: Int,
        position: Int,
        targetViewId: Int
    ): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText(
                    "View id $targetViewId at position $position in RecyclerView $recyclerViewId"
                )
            }

            override fun matchesSafely(view: View): Boolean {
                val recyclerView = view.rootView.findViewById<RecyclerView>(recyclerViewId) ?: return false
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position) ?: return false
                val targetView = viewHolder.itemView.findViewById<View>(targetViewId)
                return view === targetView
            }
        }
    }
}
