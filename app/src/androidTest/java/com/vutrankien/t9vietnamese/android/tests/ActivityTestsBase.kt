package com.vutrankien.t9vietnamese.android.tests

import android.app.Activity
import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.tests.TestHelpers.unlockScreen
import com.vutrankien.t9vietnamese.lib.LogFactory
import org.junit.Before
import org.junit.Rule

abstract class ActivityTestsBase<TActivity : Activity>(
        log: LogFactory.Log,
        @get:Rule val mActivityRule: ActivityTestRule<TActivity>
) : IntegrationBaseTest(log) {
    fun unlockScreen() = mActivityRule.activity.unlockScreen()
}