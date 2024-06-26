import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.window.ApplicationScope
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.sharedtest.SharedAppTests
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Rule
import kotlin.test.BeforeTest

class AppTests : SharedAppTests() {
    @get:Rule
    override val composeTestRule = createComposeRule()
    override fun setUpApp(): T9App {
        return DesktopT9App(object : ApplicationScope {
            override fun exitApplication() {
                Logger.tag("AppTests").debug("called")
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