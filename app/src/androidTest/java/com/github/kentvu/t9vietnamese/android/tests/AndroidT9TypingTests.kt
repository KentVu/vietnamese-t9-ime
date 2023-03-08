package com.github.kentvu.t9vietnamese.android.tests

import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.github.kentvu.sharedtest.SharedAppTests
import com.github.kentvu.t9vietnamese.android.AndroidT9App
import com.github.kentvu.t9vietnamese.android.MainActivity
import com.github.kentvu.t9vietnamese.android.tests.TestHelpers.unlockScreen
import com.github.kentvu.t9vietnamese.ui.T9App
import org.junit.Before
import org.junit.Rule

class AndroidT9TypingTests : SharedAppTests() {
    private val _composeTestRule = createAndroidComposeRule<MainActivity>()
    @get:Rule
    override val composeTestRule: ComposeContentTestRule
        get() = _composeTestRule

    override fun setUpApp(): T9App {
        return AndroidT9App(_composeTestRule.activity)
    }

    private fun unlockScreen() = _composeTestRule.activity.unlockScreen()

    @Before
    fun setUp() {
        app = setUpApp()
        unlockScreen()
    }
}