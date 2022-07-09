package com.github.kentvu.t9vietnamese.android.tests

import androidx.test.rule.ActivityTestRule
import com.github.kentvu.t9vietnamese.android.MainActivity

fun unlockScreen() = ActivityTestRule(MainActivity::class.java).activity.unlockScreen()
