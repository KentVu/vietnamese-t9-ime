package com.vutrankien.t9vietnamese.android.tests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.android.R
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
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
    }

    private val robot by lazy { Robot(mActivityRule.activity.testingHook) }

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

    @Test fun selectCandidate(): Unit = runBlocking<Unit> {
        robot.pressSequentially("24236")
            .checkTextDisplayed("chào")
            .browseTo("chào")
            .confirm()
            .checkWordConfirmed("chào")
    }

    @Test fun selectCandidateBackward(): Unit = runBlocking {
        robot.pressSequentially("24236")
            .checkTextDisplayed("chào")
            .browseTo("chào")
            .selectNext()
            .selectPrev()
            .confirm()
            .checkWordConfirmed("chào")
    }

    class Robot(
        private val testingHook: MainActivity.TestingHook
    ) {
        private val uiEventSink: SendChannel<EventWithData<Event, Key>> = testingHook.eventSink

        suspend fun pressSequentially(numSeq: String): Robot = apply {
            numSeq.forEach {
                //onView(withText(startsWith("$it"))).perform(click())
                uiEventSink.send(Event.KEY_PRESS.withData(Key.fromNum(it)))
            }
        }

        fun checkTextDisplayed(text: String):Robot = apply {
            onView(withText(text)).check(matches(isDisplayed()))
        }

        fun checkTextDisplayed(stringCondition: Matcher<String>) {
            onView(withText(stringCondition)).check(matches(isDisplayed()))
        }

        suspend fun browseTo(targetWord: String): Robot = apply {
            // Find distance from selected word to targetWord
            val distance = testingHook.candidatesAdapter.findItem(targetWord) - testingHook.candidatesAdapter.selectedWord
            repeat(distance) {
                uiEventSink.send(Event.KEY_PRESS.withData(Key.star))
            }
        }

        fun checkWordConfirmed(word: String) {
            onView(allOf(withId(R.id.editText), withText(containsString(word))))
                    .check(matches(isDisplayed()))
        }

        suspend fun selectNext() = apply {
            uiEventSink.send(Event.KEY_PRESS.withData(Key.star))
        }

        suspend fun selectPrev() = apply {
            uiEventSink.send(Event.KEY_PRESS.withData(Key.left))
        }

        suspend fun confirm() = apply {
            uiEventSink.send(Event.KEY_PRESS.withData(Key.num0))
        }
    }
}