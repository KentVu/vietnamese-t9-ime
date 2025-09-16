package com.github.kentvu.t9vietnamese.ui.test

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.util.fastJoinToString
import com.github.kentvu.t9vietnamese.model.KeyPads
import com.github.kentvu.t9vietnamese.ui.ImeServiceUI
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class T9AppEndToEndTest {
  private val runner = AppRunner()

  @Test
  fun whenDawgNotGeneratedThenEnableKeypad() = runComposeUiTest {
    with(runner) {
      startApp()
      hasKeypadEnabled()
    }
  }

  @Test
  fun type24236_candidatesNotEmpty() = runComposeUiTest {
    with(runner) {
      startApp()
      type("24236")
      candidatesContains("chào")
    }
  }

  @Test
  fun type2_candidatesContainsSingleLetterOnly() = runComposeUiTest {
    with(runner) {
      startApp()
      type("2")
      candidatesAllMatches(textHasLength1)
    }
  }

  @Test
  fun reportMissingWord_whenCandidatesEmpty_doNotDisplay() = runComposeUiTest {
    with(runner) {
      startApp()
      find(ImeServiceUI.Semantic.ShowReportUiButton).assertDoesNotExist()
      type("2")
      find(ImeServiceUI.Semantic.ShowReportUiButton).assertIsDisplayed()
      type(KeyPads.VN.keyBackspace)
      find(ImeServiceUI.Semantic.ShowReportUiButton).assertDoesNotExist()
    }
  }

  @Test
  fun reportMissingWord_open() = runComposeUiTest {
    with(runner) {
      startApp()
      type("2")
      find(ImeServiceUI.Semantic.ShowReportUiButton).performClick()
      onReportUi().isDisplayed()
    }
  }

  @Test
  fun reportMissingWord_clickReportButton_close() = runComposeUiTest {
    with(runner) {
      startApp()
      type("2")
      find(ImeServiceUI.Semantic.ShowReportUiButton).performClick()
      onReportUi().isDisplayed()
      find(ImeServiceUI.Semantic.DismissButton).performClick()
      onReportUi().assertDoesNotExist()
    }
  }

  @Test
  fun reportMissingWord_containsCurrentState() = runComposeUiTest {
    with(runner) {
      startApp()
      val c = '2'
      type(c)
      val candidatesStr = getCandidates().fastJoinToString(",", limit = 50, truncated = "")
      find(ImeServiceUI.Semantic.ShowReportUiButton).performClick()
      onReportUi().onChildren().also { it.printToLog("reportUi") }
        .filterToOne(hasTextMatches(Regex("^https?://")))
        .assertTextContains("?numSeq=$c&candidates=$candidatesStr", true)
    }
  }

  private fun hasTextMatches(regex: Regex) = SemanticsMatcher("${SemanticsProperties.Text.name} matches $regex") { node ->
    if (!node.mergingEnabled) false
    else node.config[SemanticsProperties.Text].any { regex.containsMatchIn(it) } ?: run {
      println("${node.config} does not contains Text")
      false
    }
  }

  private val textHasLength1 = SemanticsMatcher("${SemanticsProperties.Text.name} has length 1",) { node ->
    node.config[SemanticsProperties.Text].any { it.length == 1 } // TODO can be extracted to a matcher parameter
  }

}
