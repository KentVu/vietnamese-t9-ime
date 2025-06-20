package com.github.kentvu.t9vietnamese.ui.test

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertAll
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.hasTextExactly
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.waitUntilAtLeastOneExists
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.ui.CommonUI
import com.github.kentvu.t9vietnamese.ui.DesktopEnvironmentInteraction
import com.github.kentvu.t9vietnamese.ui.DesktopPresenter
import com.github.kentvu.t9vietnamese.ui.DesktopUI
import com.github.kentvu.t9vietnamese.ui.ImeServiceUI
import kotlinx.coroutines.test.TestScope
import kotlin.also
import kotlin.test.assertTrue
import kotlin.text.forEach

@OptIn(ExperimentalTestApi::class)
class AppRunner {

  private val env = DesktopEnvironmentInteraction()
  private val scope = TestScope()
  private val presenter = DesktopPresenter(scope)
  private val app = T9App(
    env,
    scope,
    presenter,
  )
  private val ui = DesktopUI(presenter) { println("app close called") }

  fun ComposeUiTest.startApp() {
    NapierLogger.init()
    //scope.launch
    //runTest
    setContent {
      LaunchedEffect(this@AppRunner) {
        app.start()
      }
      ui.AppUi()
    }
  }

  fun ComposeUiTest.hasKeypadEnabled() {
    val key = presenter.stateSource.value.keyPad.key1
    hasKeyEnabled(key)
  }

  fun ComposeUiTest.type(seq: String) {
    seq.forEach {
      type(it)
    }
  }

  fun ComposeUiTest.candidatesContains(cand: String) {
    //waitForIdle()
    waitUntilAtLeastOneExists(hasTextExactly(cand))
    onCandidates().onChildren().filterToOne(hasTextExactly(cand))
    //.assertExists()
  }

  private fun ComposeUiTest.type(c: Char) {
    val keypad = presenter.stateSource.value.keyPad
    type(keypad.findKey(c))
  }

  private fun ComposeUiTest.type(key: Key) {
    onNode(hasText(key.action.displaySymbol) and hasText(key.subChars.orEmpty()))
    .performClick()
  }

  private fun ComposeUiTest.onCandidates(): SemanticsNodeInteraction {
    //waitUntilAtLeastOneExists()
    return onNodeWithContentDescription(ImeServiceUI.Semantic.Candidates.name)
  }

  fun ComposeUiTest.candidatesAllMatches(matcher: SemanticsMatcher) {
    onCandidates().also { it.printToLog("candidatesContain") }
      .onChildren().assertAll(matcher)
  }

  fun ComposeUiTest.showsReportUi() {
    onNodeWithContentDescription(ImeServiceUI.Semantic.report_ui.name).isDisplayed()
  }

  fun ComposeUiTest.find(semantic: CommonUI.Semantic): SemanticsNodeInteraction =
    onNodeWithContentDescription(semantic.name)

  companion object {
    private fun ComposeUiTest.hasKeyEnabled(key: Key) {
      val matcher= hasText(key.action.displaySymbol) and hasText(key.subChars!!)
      onNode(matcher).also {
        waitUntil("Init", 30_000) {
          onAllNodes(matcher and isEnabled()).fetchSemanticsNodes().isNotEmpty()
        }
      }.assertHasClickAction()
      .assertIsEnabled()
      //.performClick()
      //awaitIdle()
    }
  }
}
