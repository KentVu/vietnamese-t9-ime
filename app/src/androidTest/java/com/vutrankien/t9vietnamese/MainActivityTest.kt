package com.vutrankien.t9vietnamese

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This suppose to contains UI related tests
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule val mActivityRule = ActivityTestRule<MainActivity>(MainActivity::class.java)

    @Test fun initialize() {
        val engineLoadStr = mActivityRule.activity.getString(R.string.engine_loading)
        val initializedStr = mActivityRule.activity.getString(R.string.notify_initialized)
        onView(anyOf(withText(containsString(engineLoadStr)), withText(containsString("not initialized"))))
                .check(matches(isDisplayed()))
        onView((withText(containsString(initializedStr))))
                .check(matches(isDisplayed()))
    }

    @Test fun basicTyping() {
        "24236".forEach { onView(withText(startsWith("$it"))).perform(click()) }
    }
}