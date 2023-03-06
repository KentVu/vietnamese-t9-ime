package com.github.kentvu.sharedtest

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.AppUI
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class AppTests() {
    abstract val app: T9App
    @get:Rule
    abstract val composeTestRule: ComposeContentTestRule

    @BeforeTest
    fun setUp() {
        composeTestRule.setContent {
            app.start()
        }
    }

    @AfterTest
    fun tearDown() {
        app.stop()
    }
    @Test
    fun type24_candidatesNotEmpty() = runTest {
        //setUp()
        type(24)
        assertCandidatesNotEmpty()
    }

    @Test
    fun typeClear_candidatesEmpty() = runTest {
        type(24)
        assertCandidatesNotEmpty()
        type(VNKeys.Clear)
        assertCandidatesEmpty()
    }

    @Test
    fun typeClear_reset() = runTest {
        type(24)
        assertCandidatesNotEmpty()
        type(VNKeys.Clear)
        assertCandidatesEmpty()
        type(24)
        assertCandidateDisplayed("24")
    }

    @Test
    fun `type a word`() = runTest {
        type(24236)
        assertCandidateDisplayed("chào")
    }

    @Test
    fun `confirm a word`() = runTest {
        type(24236)
        assertCandidateDisplayed("chào")
        selectItem("chào")
        //app.ui.locateCandidate("chào")
    }

    private suspend fun selectItem(s: String) = useComposeWhenIdle {
        TODO("Not yet implemented")
    }

    private suspend fun assertCandidateDisplayed(cand: String) = useComposeWhenIdle {
        onCandidates().onChildren().filterToOne(hasTextExactly(cand)).assertExists()
    }

    private suspend fun assertCandidatesNotEmpty() = useComposeWhenIdle {
        onCandidates().apply {
            printToLog("type24")
            onChildAt(0).assertExists()
        }
    }

    private suspend fun assertCandidatesEmpty() = useComposeWhenIdle {
        onCandidates().onChildren().assertCountEquals(0)
    }

    private fun ComposeContentTestRule.onCandidates() =
        onNodeWithContentDescription(AppUI.Semantic.candidates)

    private suspend fun useComposeWhenIdle(block: ComposeContentTestRule.() -> Unit) {
        composeTestRule.apply {
            awaitIdle()
            block()
        }
    }

    private fun type(key: Key) {
        composeTestRule.apply {
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .performClick()
        }
    }

    private fun type(n: Int) {
        "$n".forEach {
            val key = VNKeys.fromChar(it)
            type(key)
        }
    }
}