package com.github.kentvu.t9vietnamese.android.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.kentvu.t9vietnamese.android.view.MainActivityView
import com.github.kentvu.t9vietnamese.model.KeysCollection
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AndroidT9TypingTests {
    @get:Rule
    val composeTestRule = createComposeRule()
    // use createAndroidComposeRule<YourActivity>() if you need access to
    // an activity

    @Before
    fun setUp() {
        // Start the app
        composeTestRule.setContent {
            MainActivityView()
        }
    }

    @Test
    fun typingTest() {
        composeTestRule.onNodeWithText("${KeysCollection.key0.symbol}").performClick()

        composeTestRule.onNodeWithText("${KeysCollection.key0.symbol}").assertIsDisplayed()
    }
}