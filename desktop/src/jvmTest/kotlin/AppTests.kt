import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.window.ApplicationScope
import com.github.kentvu.sharedtest.SharedAppTests
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.ui.T9App
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Rule
import kotlin.test.BeforeTest

class AppTests : com.github.kentvu.sharedtest.SharedAppTests() {
    @get:Rule
    override val composeTestRule = createComposeRule()
    override fun setUpApp(): T9App {
        return DesktopT9App(object : ApplicationScope {
            override fun exitApplication() {
                Napier.d("called")
            }
        })
    }

    @BeforeTest
    fun setUp() {
        app = setUpApp()
        composeTestRule.setContent {
            app.ui.AppUi()
            rememberCoroutineScope { Dispatchers.Main }.launch {
                app.start()
            }
        }
    }
}