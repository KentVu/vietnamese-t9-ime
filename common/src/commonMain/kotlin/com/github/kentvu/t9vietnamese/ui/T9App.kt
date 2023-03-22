package com.github.kentvu.t9vietnamese.ui

import androidx.compose.ui.input.key.KeyEvent
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.lib.DawgTrie
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.*

abstract class T9App(
    protected val env: EnvironmentInteraction,
) {

    protected abstract val scope: CoroutineScope
    abstract val ui: AppUI
    //private val engine = Engine(ui, DecomposedVietnameseWords(env.vnWordsSource), env.fileSystem)
    private val backend by lazy { Backend(
        ui,
        DawgTrie(
            DecomposedVietnameseWords(env.vnWordsSource),
            env.fileSystem
        )
    ) }

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

    protected fun createCoroutineScope() = CoroutineScope(env.mainDispatcher + Job())
}
