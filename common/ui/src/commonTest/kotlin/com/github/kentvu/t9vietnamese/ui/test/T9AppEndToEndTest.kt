package com.github.kentvu.t9vietnamese.ui.test

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isEnabled
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.test.runComposeUiTest
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.model.KeyPads
import com.github.kentvu.t9vietnamese.ui.DesktopEnvironmentInteraction
import com.github.kentvu.t9vietnamese.ui.DesktopPresenter
import com.github.kentvu.t9vietnamese.ui.DesktopUI
import kotlinx.coroutines.CoroutineScope
import kotlin.test.Test

class T9AppEndToEndTest {
    @OptIn(ExperimentalTestApi::class)
    @Test
    fun myTest() = runComposeUiTest {
        NapierLogger.init()
        val env = DesktopEnvironmentInteraction
        val scope = CoroutineScope(env.mainDispatcher)
        val presenter = DesktopPresenter(scope)
        val app = T9App(
            env,
            scope,
            presenter,
        )
        val ui = DesktopUI(presenter) { println("app close called") }
        setContent {
            LaunchedEffect(1) {
                app.start()
            }
            ui.AppUi()
        }
        val key = presenter.stateSource.value.keyPad.key1
        val matcher= hasText(key.action.displaySymbol) and hasText(key.subChars!!)
        onNode(matcher).also {
            waitUntil("Init", 30_000) {
                //it.fetchSemanticsNode().config.getOrElse(SemanticsProperties.Disabled) { true }
                onAllNodes(matcher and isEnabled()).fetchSemanticsNodes().isNotEmpty()
            }
        }.assertHasClickAction()
        .assertIsEnabled()
        .performClick()
        //awaitIdle()
    }
}
