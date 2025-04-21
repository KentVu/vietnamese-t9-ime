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
    private val stateSource: MutableStateFlow<UI.State> = MutableStateFlow(UI.State { }),
    private val inputConnection: InputSystemConnection = DefaultInputConnection(stateSource),
) {

    private val backend = Backend(
        DawgTrie(
            DecomposedVietnameseWords(env.vnWordsSource),
            env.fileSystem
        ),
        stateSource,
        inputConnection,
    )

    fun start() {
        ui.init(stateSource)
        scope.launch(env.ioDispatcher) {
            backend.init()
        }
    }

    fun stop() {
    }

    companion object {
        /*fun create(
            env: EnvironmentInteraction,
            scope: CoroutineScope = CoroutineScope(env.mainDispatcher + Job()),
            stateSource: MutableStateFlow<UI.State> = MutableStateFlow(UI.State { }),
            ui: UI,
        ): T9App {
            return T9App(env, scope, stateSource, ui).also {
                ui.init(stateSource)
            }
        }*/
    }

    class DefaultInputConnection(
        private val stateSource : MutableStateFlow<UI.State>
    ): InputSystemConnection {
        override fun commitText(text: String) {
            stateSource.update { copy(
                confirmedText = confirmedText + text
            )}
        }

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) {
            stateSource.update { copy(
                confirmedText = confirmedText.dropLast(1)
            ) }
        }

        override fun performEditorAction() {
            log.info("TODO(performEditorAction)")
        }
        companion object {
            private val log = Logger.tag("DefaultInputConnection")
        }
    }

}
