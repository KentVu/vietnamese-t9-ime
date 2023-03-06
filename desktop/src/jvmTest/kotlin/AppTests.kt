import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.window.ApplicationScope
import com.github.kentvu.sharedtest.SharedAppTests
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.ui.T9App
import io.github.aakira.napier.Napier
import org.junit.Rule
import kotlin.test.BeforeTest

class AppTests : SharedAppTests() {
    @get:Rule
    override val composeTestRule = createComposeRule()
    override fun setUpApp(): T9App {
        return DesktopT9App(object : ApplicationScope {
            override fun exitApplication() {
                Napier.d("called")
            }
        })
    }

}