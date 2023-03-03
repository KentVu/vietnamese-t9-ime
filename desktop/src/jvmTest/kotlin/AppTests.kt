import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.github.kentvu.t9vietnamese.desktop.DesktopT9App
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppTests {
    @get:Rule
    val composeTestRule = createComposeRule()
    private lateinit var app: DesktopT9App

    @Test
    fun testInitialization() = runTest {
        composeTestRule.apply {
            app.ensureBackendInitialized()
            val key = VNKeys.key2
            //onRoot().printToLog("Test")
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .assertHasClickAction()
                .assertIsEnabled()
                .performClick()
                .printToLog("Test")
            awaitIdle()
        }
    }

    @Before
    fun setUp() {
        app = DesktopT9App()
        composeTestRule.setContent {
            app.startForTest()
        }
    }

    @Test
    fun type24_candidatesNotEmpty() = runTest {
        //setUp()
        type(24)
        assertCandidatesNotEmpty()
    }

    @Test
    fun typeClear_candidatesEmpty() = runTest {
        type(24)
        assertCandidatesNotEmpty()
        type(VNKeys.Clear)
        assertCandidatesEmpty()
    }

    private suspend fun assertCandidatesNotEmpty() = useComposeWhenIdle {
        onCandidates().apply {
            printToLog("type24")
            onChildAt(0).assertExists()
        }
    }

    private suspend fun assertCandidatesEmpty() = useComposeWhenIdle {
        onCandidates().onChildren().assertCountEquals(0)
    }

    private fun ComposeContentTestRule.onCandidates() =
        onNodeWithContentDescription(Semantic.candidates)

    private suspend fun useComposeWhenIdle(block: ComposeContentTestRule.() -> Unit) {
        composeTestRule.apply {
            awaitIdle()
            block()
        }
    }

    private fun type(key: Key) {
        composeTestRule.apply {
            onNode(hasText("${key.symbol}") and hasText(key.subChars))
                .performClick()
        }
    }

    private fun type(n: Int) {
        "$n".forEach {
            val key = VNKeys.fromChar(it)
            type(key)
        }
    }
}