import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App

fun main() {
    application {
        val app = DesktopT9App(this)
        LaunchedEffect(1) {
            app.start()
        }
        Window(
            onCloseRequest = app::onCloseRequest,
            title = "Compose for Desktop",
            state = rememberWindowState(width = 300.dp, height = 600.dp),
            onKeyEvent = app::onKeyEvent
        ) {
            app.ui.AppUi()
        }
    }
}
