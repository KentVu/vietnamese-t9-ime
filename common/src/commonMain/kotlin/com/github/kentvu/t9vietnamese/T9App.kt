package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.DawgTrie
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import okio.Source

class T9App(
    protected val env: EnvironmentInteraction,
    protected val scope: CoroutineScope,
    val presenter: Presenter,
) {
    //protected val scope: CoroutineScope = ui.scope

    private val backend = Backend(
        DawgTrie(
            DecomposedVietnameseWords(env.vnWordsSource),
            env.fileSystem
        ),
        presenter,
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
