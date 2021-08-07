package com.vutrankien.t9vietnamese.android.tests

import androidx.test.rule.ActivityTestRule
import com.vutrankien.t9vietnamese.android.MainActivity
import com.vutrankien.t9vietnamese.lib.LogFactory

abstract class MainActivityTestsBase(log: LogFactory.Log)
    : ActivityTestsBase<MainActivity>(
        log, ActivityTestRule(MainActivity::class.java)
) {
    protected val robot by lazy { Robot(log, mActivityRule.activity.testingHook) }
}
