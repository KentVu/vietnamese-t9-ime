import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.model.VNKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun composeTest() = runTest {
        val app = DesktopT9App()
        composeTestRule.setContent {
            app.startForTest()
        }
        //delay(3000)
        composeTestRule.apply {
            app.ensureBackendInitialized()
            val key = VNKeys.key2
            //Napier.d(onRoot().printToString())
            onRoot().printToLog("Test")
            //onNodeWithText("${key.symbol}${key.subChars}").assertIsEnabled()
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .assertHasClickAction()
                .assertIsEnabled()
                .performClick()
                .printToLog("Test")
            awaitIdle()
        }
    }
}