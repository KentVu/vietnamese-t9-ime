//@file:Suppress("IllegalIdentifier")
package com.vutrankien.t9vietnamese.android.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.AndroidLogFactory
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
class DefectsTest: MainActivityTestsBase(
        AndroidLogFactory.newLog("DefectsTest")
) {

    @Before
    fun setup() {
        unlockScreen()
    }

    @Test fun crashWhenNavigatingBackTooMuch(): Unit = runBlocking {
        robot
                .pressSequentially("2423**<<<")
                .confirm()
    }
}