import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import kotlinx.coroutines.flow.MutableStateFlow

//actual fun getPlatformName(): String = "Desktop"

@Preview
@Composable
fun AppPreview() {
    AppUi(MutableStateFlow(true)) {}
}