/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.android.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.IdlingResource
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.android.R
import com.vutrankien.t9vietnamese.android.T9Application
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
    fun setup() {
        //(mActivityRule.activity.application as T9Application).appComponent...
        //IdlingRegistry.getInstance().register(object : IdlingResource {
        //    override fun getName(): String {
        //        return "MainActivityTestIdling"
        //    }
        //
        //    override fun isIdleNow(): Boolean {
        //        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //    }
        //
        //    override fun registerIdleTransitionCallback(callback: IdlingResource.ResourceCallback?) {
        //        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //    }
        //
        //})
    }

    @Test fun initialize() {
        val engineLoadStr = mActivityRule.activity.getString(R.string.engine_loading)
        val initializedStr = mActivityRule.activity.getString(R.string.notify_initialized)
        // Drop the progress string
        onView(anyOf(withText(containsString(engineLoadStr.dropLast(7)))))
                .check(matches(isDisplayed()))
        onView((withText(containsString(initializedStr))))
                .check(matches(isDisplayed()))
    }

    @Test fun basicTyping() {
        "24236".forEach { onView(withText(startsWith("$it"))).perform(click()) }
        onView(withText("chào")).check(matches(isDisplayed()))
    }
}