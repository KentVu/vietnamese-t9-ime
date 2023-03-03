import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.github.kentvu.t9vietnamese.ui.UIState

//actual fun getPlatformName(): String = "Desktop"

@Preview
@Composable
fun AppPreview() {
    AppUi(mutableStateOf(UIState())) {}
}