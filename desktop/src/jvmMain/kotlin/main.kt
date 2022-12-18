import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

@OptIn(ExperimentalComposeUiApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Compose for Desktop",
        state = rememberWindowState(width = 300.dp, height = 300.dp),
        onKeyEvent = {
            if (it.type == KeyEventType.KeyUp && it.isCtrlPressed && it.key == Key.A) {
                println("Ctrl + A is pressed")
                true
            } else {
                // let other handlers receive this event
                false
            }
        }
    ) {
        AppUi()
    }
}