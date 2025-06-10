package com.github.kentvu.t9vietnamese.ui.test

import androidx.compose.ui.test.ExperimentalTestApi
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
      candidatesAllMatches(Regex("^\\w$"))
    }
  }

}
