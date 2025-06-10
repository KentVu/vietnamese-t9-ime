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
    LaunchedEffect(app) { }
    Main {
      Div({ classes("container", "py-5") }) {
        H2({ classes("pb-2", "px-4", "border-bottom") }) {
          Img(src = "ic_t9.svg", attrs = {
            classes("bi")
            attr("width", "32")
            attr("height", "32")
            //style { width(1.em); height(1.em) }
          })
          Text("T9 keypad demo")
        }
        Div({
          classes("row", "g-4", "row-cols-1", "row-cols-lg-3")
        }) {
          Div({
            classes("feature", "col")
          }) {
            Div({
              classes("feature-icon", "d-inline-flex", "align-items-center", "justify-content-center", "text-bg-primary", "bg-gradient", "fs-2", "mb-3")
            }) {
              //Text(Res.getUri("files/vi-DauMoi.dic"))
            }
          }
        }
        Div({ classes("row") }) {
          Div({ classes("col-md-4") }) {
            ui.Emulator(inputConnection.confirmedText)
          }
        }
      }
    }
  }
}
