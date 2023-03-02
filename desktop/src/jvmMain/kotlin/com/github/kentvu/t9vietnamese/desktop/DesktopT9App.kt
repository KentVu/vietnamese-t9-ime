package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem

class DesktopT9App(
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) {
    private var applicationScope: ApplicationScope? = null
    private val ui = DesktopUI({ applicationScope?.exitApplication() }, scope)
    private val backend = Backend(
        ui,
        VietnameseWordList,
        FileSystem.SYSTEM
    )

    fun start() {
        Napier.base(DebugAntilog())
        application {
            applicationScope = this
            LaunchedEffect(1) {
                backend.init()
            }
            Window(
                onCloseRequest = {
                    ui.eventSource.tryEmit(UIEvent.CloseRequest)
                },
                title = "Compose for Desktop",
                state = rememberWindowState(width = 300.dp, height = 600.dp),
                onKeyEvent = ui::onKeyEvent
            ) {
                ui.buildUi()
            }
        }
    }

    @Composable
    fun startForTest() {
        Napier.base(DebugAntilog())
        LaunchedEffect(1) {
            backend.init()
        }
        ui.buildUi()
    }

    fun ensureBackendInitialized() {
        backend.ensureInitialized()
    }
}
