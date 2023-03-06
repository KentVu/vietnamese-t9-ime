import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.kentvu.t9vietnamese.UIEvent
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

fun main() {
    val app = DesktopT9App(
        CoroutineScope(Dispatchers.Main),
        Dispatchers.IO,
        { finish() },
        VietnameseWordList,
        AndroidFileSystem(applicationContext),
        object : T9App.ComposeClosure {
            override fun setContent(block: @Composable () -> Unit) {
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

        }
    )
    private val app by lazy {
    }
    app.start()
}
