package com.github.kentvu.t9vietnamese.android.tests

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.github.kentvu.sharedtest.SharedAppTests
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.T9App
import com.github.kentvu.t9vietnamese.test.AppTests
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AndroidT9TypingTests : SharedAppTests() {
    @get:Rule
    override val composeTestRule = createComposeRule()
    //val appTests = AppTests(T9App(), composeTestRule)
}