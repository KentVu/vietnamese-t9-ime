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
import com.vutrankien.t9vietnamese.lib.LogFactory
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

    private val log: LogFactory.Log by lazy { mActivityRule.activity.logFactory.newLog("MainActivityTest") }

    @Before
    fun unlockScreen() = mActivityRule.activity.unlockScreen()

    @Before
    fun setup() {
    }

    private val robot by lazy { Robot(log, mActivityRule.activity.testingHook) }

    @Test fun initialize() {
        val engineLoadStr = mActivityRule.activity.getString(R.string.engine_loading)
        val initializedStr = mActivityRule.activity.getString(R.string.notify_initialized)
        // Drop the progress string
        robot.checkCandidateDisplayed(anyOf(containsString(engineLoadStr.dropLast(7)), containsString(initializedStr)))
    }

    @Test fun basicTyping(): Unit = runBlocking<Unit> {
        robot
            .pressSequentially("24236")
            .checkCandidateDisplayed("chào")
    }

    @Test fun selectCandidate(): Unit = runBlocking<Unit> {
        robot.pressSequentially("24236")
            .checkCandidateDisplayed("chào")
            .browseTo("chào")
            .confirm()
            .checkWordConfirmed("chào")
    }

    @Test fun selectCandidateBackward(): Unit = runBlocking {
        robot.pressSequentially("24236")
            .checkCandidateDisplayed("chào")
            .browseTo("chào")
            .selectNext()
            .selectPrev()
            .confirm()
            .checkWordConfirmed("chào")
        // wait if necessary
    }

    class Robot(
        private val log: LogFactory.Log,
        private val testingHook: MainActivity.TestingHook
    ) {
        private val uiEventSink: SendChannel<EventWithData<Event, Key>> = testingHook.eventSink

        suspend fun pressSequentially(numSeq: String): Robot = apply {
            numSeq.forEach {
                pressAndCheck(it)
            }
        }

        private suspend fun pressAndCheck(key: Char) {
            //onView(withText(startsWith("$it"))).perform(click())
            uiEventSink.send(Event.KEY_PRESS.withData(Key.fromNum(key)))
            // TODO wait for completion
            //testingHook.waitNewCandidates()
        }

        fun checkCandidateDisplayed(candidate: String):Robot = apply {
            //Assert.assertTrue("checkCandidateDisplayed:$candidate", testingHook.candidatesAdapter.findItem(candidate)  != -1)
            onView(withText(candidate)).check(matches(isDisplayed()))
            //onData(withText(candidate)).check(matches(isDisplayed()))
        }

        fun checkCandidateDisplayed(stringCondition: Matcher<String>) {
            onView(withText(stringCondition)).check(matches(isDisplayed()))
        }

        suspend fun browseTo(targetWord: String): Robot = apply {
            // Find distance from selected word to targetWord
            val distance = testingHook.candidatesAdapter.findItem(targetWord) - testingHook.candidatesAdapter.selectedWord
            log.d("browseTo:distance=$distance")
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