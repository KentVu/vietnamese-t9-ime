package com.vutrankien.t9vietnamese.android.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.android.R
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.anyOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
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

    private val robot by lazy { Robot(mActivityRule.activity.testingHook.eventSink) }

    @Test fun initialize() {
        val engineLoadStr = mActivityRule.activity.getString(R.string.engine_loading)
        val initializedStr = mActivityRule.activity.getString(R.string.notify_initialized)
        // Drop the progress string
        robot.checkTextDisplayed(anyOf(containsString(engineLoadStr.dropLast(7)), containsString(initializedStr)))
    }

    @Test fun basicTyping(): Unit = runBlocking<Unit> {
        robot
            .pressSequentially("24236")
            .checkTextDisplayed("chào")
    }

    class Robot(
        private val eventSink: SendChannel<EventWithData<Event, Key>>
    ) {
        suspend fun pressSequentially(numSeq: String): Robot = apply {
            numSeq.forEach {
                //onView(withText(startsWith("$it"))).perform(click())
                eventSink.send(Event.KEY_PRESS.withData(Key.fromNum(it)))
            }
        }

        fun checkTextDisplayed(text: String) {
            onView(withText(text)).check(matches(isDisplayed()))
        }

        fun checkTextDisplayed(stringCondition: Matcher<String>) {
            onView(withText(stringCondition)).check(matches(isDisplayed()))
        }
    }
}