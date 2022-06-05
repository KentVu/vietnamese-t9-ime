package com.github.kentvu.t9vietnamese.android

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.kentvu.t9vietnamese.android.view.MainActivityView
import com.github.kentvu.t9vietnamese.model.StandardKeys
import org.junit.Rule
import org.junit.Test

class T9Tests {
    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Test
    fun myTest() {
        // Start the app
        composeTestRule.setContent {
            MainActivityView()
        }

        composeTestRule.onRoot().printToLog("semantics")
        composeTestRule.onNodeWithText("${StandardKeys.key0.symbol}").performClick()

        composeTestRule.onNodeWithText("${StandardKeys.key0.symbol}").assertIsDisplayed()
    }
}