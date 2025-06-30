package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.ImeServicePresenter
import com.github.kentvu.t9vietnamese.KeypadEvent
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.tests.TestPresenter
import org.jetbrains.compose.web.testutils.ComposeWebExperimentalTestsApi
import org.jetbrains.compose.web.testutils.runTest
import kotlin.collections.last
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowserUITest {
  @Test
  fun `send onStartInputView properly`() {
    val presenter = TestPresenter()
    val ui = BrowserUI(presenter)
    val info = ::EditorInfo
    val ev = KeypadEvent::InputViewStart

    for(mode in setOf(EditorInfo.Class.Normal, EditorInfo.Class.Number)) {
      ui.onStartInputView(info(mode))
      assertEquals(ev(info(mode)), presenter.evHistory.last())
    }
  }

  // https://github.com/JetBrains/compose-multiplatform/blob/master/tutorials/HTML/Using_Test_Utils/README.md
  @OptIn(ComposeWebExperimentalTestsApi::class)
  @Test
  fun showReportMissingWordButton() = runTest {
    val scope = kotlinx.coroutines.MainScope()
    val inputConnection = FakeInputConnection()
    val presenter = ImeServicePresenter(
      scope,
      inputConnection = inputConnection,
    )
    val ui = BrowserUI(presenter) 
    NapierLogger.init()
    val env = TestBrowserEnvironmentInteraction
    val app = T9App(
      env,
      scope,
      presenter,
    )

    console.log("TODO app.start() when resource are available in karma\n" +
      "https://youtrack.jetbrains.com/issue/KT-42923/KJS-Resources-are-not-available-when-running-Karma-tests")
    composition {
      ui.Emulator(inputConnection.confirmedTextState)
    }

    val runner = BrowserAppRunner(root)
    runner.showsReportButton(enabled = false)
  }

  /*@Test
  fun thingsShouldWork() {
    assertEquals(listOf(1,2,3).reversed(), listOf(3,2,1))
  }

  @Test
  fun thingsShouldBreak() {
    assertEquals(listOf(1,2,3).reversed(), listOf(1,2,3))
  }*/
}
