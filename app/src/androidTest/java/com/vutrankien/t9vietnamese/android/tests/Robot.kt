package com.vutrankien.t9vietnamese.android.tests

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.android.R
import com.vutrankien.t9vietnamese.lib.Event
import com.vutrankien.t9vietnamese.lib.EventWithData
import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.LogFactory
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.delay
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher

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
        delay(500)
    }

    fun checkCandidateDisplayed(candidate: String): Robot = apply {
        TestHelpers.waitUntilViewFound(ViewMatchers.withText(candidate), 1000)
    }

    fun checkCandidateDisplayed(stringCondition: Matcher<String>) {
        Espresso.onView(ViewMatchers.withText(stringCondition)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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
        TestHelpers.waitUntilViewFound(CoreMatchers.allOf(ViewMatchers.withId(R.id.editText), ViewMatchers.withText(CoreMatchers.containsString(word))), 1000)
        //onView(allOf(withId(R.id.editText), withText(containsString(word)))).check(matches(isDisplayed()))
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