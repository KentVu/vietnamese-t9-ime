package com.github.kentvu.t9vietnamese.web

import androidx.compose.runtime.LaunchedEffect
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.FakeInputConnection
import com.github.kentvu.t9vietnamese.ImeServicePresenter
import com.github.kentvu.t9vietnamese.T9App
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
//import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Main
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.renderComposable

fun main() {
  val scope = MainScope()
  val inputConnection = FakeInputConnection()
  val env = BrowserEnvironmentInteraction
  val presenter = ImeServicePresenter(
    scope,
    inputConnection = inputConnection,
  )
  val app = T9App(
    env,
    scope,
    presenter,
  )
  val ui = BrowserUI(presenter)
  NapierLogger.init()

  scope.launch {
    app.start()
  }

  renderComposable(rootElementId = "root") {
    ui.Emulator(inputConnection.confirmedTextState)
    Div({classes("row")}) { }
    /*Div({classes("row")}) {
      Div({classes("col")}) {
        Button() {
          Text("Report")
        }
      }
    }*/
  }
}
