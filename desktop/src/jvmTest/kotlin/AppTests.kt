import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import java.nio.charset.Charset

@OptIn(ExperimentalCoroutinesApi::class)
class AppTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInitialization() = runTest {
        val app = DesktopT9App()
        composeTestRule.setContent {
            app.startForTest()
        }
        //delay(3000)
        println("${Charset.defaultCharset()}")
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

    @Test
    fun type24_candidatesNotEmpty() = runTest {
        val app = DesktopT9App()
        composeTestRule.setContent {
            app.startForTest()
        }
        type(2)
        type(4)
        assertCandidatesNotEmpty()
        //type(VNKeys.C)
        //assertCandidatesEmpty()
    }

    private suspend fun assertCandidatesNotEmpty() {
        composeTestRule.apply {
            awaitIdle()
            onNodeWithContentDescription(Semantic.candidates).apply {
                printToLog("type24")
                onChildAt(0).assertExists()
            }
        }
    }

    private fun assertCandidatesEmpty() {
        TODO("Not yet implemented")
    }

    private fun assertDisplayed(s: String) {
        composeTestRule.onNodeWithText(s)
    }

    private fun type(key: Key) {
        composeTestRule.apply {
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .performClick()
        }
    }

    private fun type(n: Int) {
        val key = VNKeys.fromChar(n.digitToChar())
        type(key)
    }
}