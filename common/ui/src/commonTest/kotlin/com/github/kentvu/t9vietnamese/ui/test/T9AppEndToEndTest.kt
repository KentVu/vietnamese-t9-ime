package com.github.kentvu.t9vietnamese.ui.test

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.runComposeUiTest
import kotlin.test.Test
import kotlin.text.Regex

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

  private val textHasLength1 = SemanticsMatcher("${SemanticsProperties.Text.name} has length 1",) { node ->
    node.config[SemanticsProperties.Text].any { it.length == 1 }
  }

}
