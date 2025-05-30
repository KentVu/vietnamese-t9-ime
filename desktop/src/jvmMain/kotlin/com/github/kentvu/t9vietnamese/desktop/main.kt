package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.ui.DesktopEnvironmentInteraction
import com.github.kentvu.t9vietnamese.ui.DesktopPresenter
import com.github.kentvu.t9vietnamese.ui.DesktopUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun main() {
    NapierLogger.init()
    /*val root = runOnUiThread {
        RootComponent.Default(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
        )
    }*/
    val scope = CoroutineScope(Dispatchers.Main)
    val presenter = DesktopPresenter(scope)
    val app by lazy {
        T9App(
            DesktopEnvironmentInteraction,
            scope,
            presenter,
        )
    }
    application {
        val ui = DesktopUI(presenter) { exitApplication() }
        LaunchedEffect(1) {
            app.start()
        }
        Window(
            onCloseRequest = app::stop,
            title = "Compose for Desktop",
            state = rememberWindowState(width = 300.dp, height = 600.dp),
            onKeyEvent = {
                if (it.awtEventOrNull?.keyChar == '*') {
                    ui.onKeyEvent(KeyEvent(it.nativeKeyEvent))
                }
                ui.onKeyEvent(it)
            }
        ) {
            ui.AppUi()
        }
    }
}
