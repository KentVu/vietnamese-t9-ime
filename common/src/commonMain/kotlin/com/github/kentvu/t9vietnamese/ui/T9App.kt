package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.KeyEvent
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.model.WordList
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okio.FileSystem

class T9App(
    private val scope: CoroutineScope,
    private val ioContext: CoroutineDispatcher = Dispatchers.Default,
    exitApplication: () -> Unit,
    wordlist: WordList,
    fileSystem: FileSystem,
    private val composeClosure: ComposeClosure
) {

    val ui = AppUI(scope, exitApplication)
    private val backend = Backend(
        ui,
        wordlist,
        fileSystem
    )

    fun start() {
        Napier.base(DebugAntilog())
        scope.launch(ioContext) {
            backend.init()
        }
        composeClosure.setContent {
            ui.AppUi()
        }
    }

    fun stop() {
        Napier.takeLogarithm()
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return ui.onKeyEvent(event)
    }

    interface ComposeClosure {
        fun setContent(block: @Composable () -> Unit)
    }
}
