package com.github.kentvu.t9vietnamese.ui

import androidx.compose.ui.input.key.KeyEvent
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.*

abstract class T9App(
    private val env: EnvironmentInteraction,
) {

    private val scope = CoroutineScope(env.mainDispatcher + Job())
    val ui = AppUI(scope, this)
    private val backend = Backend(
        ui,
        DecomposedVietnameseWords(env.vnWordsSource),
        env.fileSystem
    )

    fun start() {
        scope.launch(env.ioDispatcher) {
            backend.init()
        }
    }

    fun stop() {
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return ui.onKeyEvent(event)
    }

    fun finish() {
        env.finish()
    }
}
