import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.desktop.DesktopUI
import com.github.kentvu.t9vietnamese.lib.VNT9App
import com.github.kentvu.t9vietnamese.model.T9AppEvent
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.UIState
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.*
import okio.FileSystem

fun main() {
    val app = DesktopT9App()
    app.start()
}

@OptIn(ExperimentalComposeUiApi::class)
fun old_main() = application {
    Napier.base(DebugAntilog())
    DesktopUI({})
    val app = VNT9App(
        VietnameseWordList,
        FileSystem.SYSTEM
    )
    var appInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(1) {
        withContext(Dispatchers.IO) {
            app.init()
        }
        appInitialized = true
    }
    val scope = CoroutineScope(Dispatchers.Default)
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
        AppUi(mutableStateOf(UIState())) { key ->
            scope.launch {
                Napier.d("type: ${key.symbol}")
                app.type(key).collect {
                    when(it) {
                        is T9AppEvent.UpdateCandidates ->
                            Napier.d("${key.symbol}: ${it.candidates}")
                        else ->
                            Napier.d("$it")
                    }
                }
            }
        }
    }
}