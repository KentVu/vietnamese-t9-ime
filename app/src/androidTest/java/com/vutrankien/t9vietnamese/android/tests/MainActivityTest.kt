package com.vutrankien.t9vietnamese.android.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import org.hamcrest.CoreMatchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This suppose to contains UI related tests
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule val mActivityRule = ActivityTestRule<MainActivity>(
        MainActivity::class.java)

    @Before
    fun unlockScreen() = mActivityRule.activity.unlockScreen()

    @Before
    fun setup() {}

    @Test fun initialize() {
        val engineLoadStr = mActivityRule.activity.getString(R.string.engine_loading)
        val initializedStr = mActivityRule.activity.getString(R.string.notify_initialized)
        //onView(anyOf(withText(containsString(engineLoadStr)), withText(containsString("not initialized"))))
        //        .check(matches(isDisplayed()))
        onView((withText(containsString(initializedStr))))
                .check(matches(isDisplayed()))
    }

    @Test fun basicTyping() {
        "24236".forEach { onView(withText(startsWith("$it"))).perform(click()) }
    }
}