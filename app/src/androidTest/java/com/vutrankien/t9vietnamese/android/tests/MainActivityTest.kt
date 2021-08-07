package com.vutrankien.t9vietnamese.android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.android.R
import com.vutrankien.t9vietnamese.android.tests.TestHelpers.unlockScreen
import com.vutrankien.t9vietnamese.lib.LogFactory
import kotlinx.coroutines.runBlocking
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

    @Test fun selectPrevCandidateWhenNotTyping(): Unit = runBlocking {
        robot.selectPrev()
            .selectPrev()
            .checkNoCandidatesDisplayed()
            .confirm()
            .checkConfirmedWord(" ")
        // wait if necessary
    }

}