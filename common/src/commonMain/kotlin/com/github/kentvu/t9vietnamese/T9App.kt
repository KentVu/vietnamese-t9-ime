package com.github.kentvu.t9vietnamese

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.lib.DawgTrie
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.lib.update
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow

class T9App(
    protected val env: EnvironmentInteraction,
    protected val scope: CoroutineScope = CoroutineScope(env.mainDispatcher + Job()),
    val ui: UI,
) {

    private val backend = Backend(
        DawgTrie(
            DecomposedVietnameseWords(env.vnWordsSource),
            env.fileSystem
        ),
        ui,
    )

    fun start() {
        scope.launch(env.ioDispatcher) {
            backend.init()
        }
    }

    fun stop() {
    }

    companion object

}
