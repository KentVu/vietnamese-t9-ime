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
import com.github.kentvu.t9vietnamese.ui.T9App
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import okio.FileSystem

class DesktopT9App(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
): T9App(scope, Dispatchers.IO) {
    private var applicationScope: ApplicationScope? = null
    override val ui = DesktopUI { applicationScope?.exitApplication() }
    override val backend = Backend(
            ui,
            VietnameseWordList,
            FileSystem.SYSTEM
        )

    override fun composeClosure(block: @Composable () -> Unit) {
        application {
            applicationScope = this
            Window(
                onCloseRequest = {
                    ui.injectEvent(UIEvent.CloseRequest)
                },
                title = "Compose for Desktop",
                state = rememberWindowState(width = 300.dp, height = 600.dp),
                onKeyEvent = ui::onKeyEvent
            ) {
                block()
            }
        }

    }

    @Composable
    fun startForTest() {
        //Napier.base(DebugAntilog("T9Desktop"))
        LaunchedEffect(1) {
            backend.init()
        }
        ui.buildUi()
    }

    fun ensureBackendInitialized() {
        backend.ensureInitialized()
    }

    override fun stop() {
        scope.cancel("App.Stop")
        Napier.takeLogarithm()
    }
}
