package com.github.kentvu.t9vietnamese.ui

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

abstract class T9App(
    private val scope: CoroutineScope,
    private val ioContext: CoroutineDispatcher = Dispatchers.Default,
    wordlist: WordList,
    fileSystem: FileSystem,
) {

    val ui = AppUI(scope, this)
    private val backend = Backend(
        ui,
        wordlist,
        fileSystem
    )

    fun start() {
        scope.launch(ioContext) {
            backend.init()
        }
    }

    fun stop() {
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return ui.onKeyEvent(event)
    }

    abstract fun onCloseRequest()
    fun ensureBackendInitialized() {
        backend.ensureInitialized()
    }

    interface ComposeClosure {
    }
}
