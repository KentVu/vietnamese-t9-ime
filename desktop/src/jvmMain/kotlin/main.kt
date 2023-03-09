import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    application {
        val app = DesktopT9App(this)
        LaunchedEffect(1) {
            Napier.base(DebugAntilog())
            app.start()
        }
        Window(
            onCloseRequest = app::onCloseRequest,
            title = "Compose for Desktop",
            state = rememberWindowState(width = 300.dp, height = 600.dp),
            onKeyEvent = {
                if (it.awtEventOrNull?.keyChar == '*') {
                    app.onKeyEvent(KeyEvent(it.nativeKeyEvent))
                }
                app.onKeyEvent(it)
            }
        ) {
            app.ui.AppUi()
        }
    }
}
