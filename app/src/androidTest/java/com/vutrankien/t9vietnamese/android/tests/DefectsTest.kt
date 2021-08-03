//@file:Suppress("IllegalIdentifier")
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
class DefectsTest {
    @get:Rule val mActivityRule = ActivityTestRule<MainActivity>(
        MainActivity::class.java)

    private val log: LogFactory.Log by lazy { mActivityRule.activity.logFactory.newLog("MainActivityTest") }

    @Before
    fun unlockScreen() = mActivityRule.activity.unlockScreen()

    @Before
    fun setup() {
    }

    private val robot by lazy { Robot(log, mActivityRule.activity.testingHook) }


    @Test fun crashWhenNavigatingBackTooMuch(): Unit = runBlocking<Unit> {
        robot
                .pressSequentially("2423**<<<")
                .confirm()
    }
}