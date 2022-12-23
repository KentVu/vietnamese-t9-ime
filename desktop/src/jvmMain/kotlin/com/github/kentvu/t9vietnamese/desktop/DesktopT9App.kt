package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.FileSystem

class DesktopT9App {
    private val ui = DesktopUI()
    private val backend = Backend(
        ui,
        VietnameseWordList,
        FileSystem.SYSTEM
    )
    private val scope = CoroutineScope(Dispatchers.Default)

    private fun onKeyEvent(keyEvent: KeyEvent): Boolean {
        return ui.publishEvent(keyEvent)
    }

    fun start() {
        Napier.base(DebugAntilog())
        scope.launch {
            backend.init()
        }
        application {
            Window(
                onCloseRequest = ::exitApplication,
                title = "Compose for Desktop",
                state = rememberWindowState(width = 300.dp, height = 300.dp),
                onKeyEvent = ::onKeyEvent
            ) {
                ui.ui()
            }
        }
    }

}
