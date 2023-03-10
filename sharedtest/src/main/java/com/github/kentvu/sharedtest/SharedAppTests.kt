package com.github.kentvu.sharedtest

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.ui.AppUI
import com.github.kentvu.t9vietnamese.ui.T9App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import kotlin.test.AfterTest

@OptIn(ExperimentalCoroutinesApi::class)
abstract class SharedAppTests {
    @get:Rule
    abstract val composeTestRule: ComposeContentTestRule
    protected lateinit var app: T9App

    abstract fun setUpApp(): T9App


    @AfterTest
    fun tearDown() {
        app.stop()
    }

    // The keyboard is not enabled until backend is initialized
    @Test
    fun testInitialization_ensureKeyboardEnabled() = runTest {
        composeTestRule.apply {
            val key = VNKeys.key2
            //onRoot().printToLog("Test")
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .assertHasClickAction()
                .assertIsEnabled()
                .performClick()
                .printToLog("Test")
            awaitIdle()
        }
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
    fun type_a_word() = runTest {
        type(24236)
        assertCandidateDisplayed("chào")
    }

    @Test
    fun confirm_a_word() = runTest {
        type(24236)
        val cand = "chào"
        assertCandidateDisplayed(cand)
        selectCandidate(cand)
        type(0)
        checkWordIsConfirmed(cand)
        //app.ui.locateCandidate("chào")
    }

    private suspend fun checkWordIsConfirmed(cand: String) = useComposeWhenIdle {
        onNodeWithContentDescription(AppUI.Semantic.testOutput)
            .assertTextEquals("$cand ")
    }

    private suspend fun selectCandidate(cand: String) = useComposeWhenIdle {
        //repeat(10) {
        for (i in 0..10) {
            type('*')
            if (selectedCandidate() == cand) break//return@repeat
        }
        //logd("selectedCandidate:${selectedCandidate()}")
        //Napier.use {  }
        //onCandidates().onChildren().onLast().fetchSemanticsNode().config.text.printToLog("Test")
    }

    private suspend fun selectedCandidate(): String = useComposeWhenIdle {
        try {
            onNodeWithContentDescription(AppUI.Semantic.selectedCandidate).run {
                printToLog("selectedCandidate")
                fetchSemanticsNode().config.also {
                    Napier.d("selectedCandidate:$it")
                }
                    .getOrNull(SemanticsProperties.Text)?.first()?.text ?: ""

            }
        } catch (e: AssertionError) {
            Napier.w("No selectedCandidate!!")
            ""
        }
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

    private suspend fun <T> useComposeWhenIdle(block: suspend ComposeContentTestRule.() -> T): T =
        composeTestRule.run {
            awaitIdle()
            block()
        }

    private fun type(key: Key) {
        composeTestRule.apply {
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .performClick()
        }
    }

    private fun type(n: Int) {
        "$n".forEach(::type)
    }

    private fun type(c: Char) {
        val key = VNKeys.fromChar(c)
        type(key)
    }

    companion object {
        @JvmStatic
        @BeforeClass
        fun beforeClass() {
            Napier.base(DebugAntilog())
        }

        @JvmStatic
        @AfterClass
        fun afterClass() {
            Napier.takeLogarithm()
        }
    }
}

private suspend fun Napier.use(block: suspend Napier.() -> Unit) {
    base(DebugAntilog())
    block()
    takeLogarithm()
}
