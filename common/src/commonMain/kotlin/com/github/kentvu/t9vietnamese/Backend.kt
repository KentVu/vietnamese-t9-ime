package com.github.kentvu.t9vietnamese

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.lib.T9Engine
import com.github.kentvu.t9vietnamese.lib.update
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.Trie
import kotlinx.coroutines.flow.MutableStateFlow

class Backend(
    private val trie: Trie,
    private val stateSource : MutableStateFlow<UI.State>,
    inputConnection: InputSystemConnection,
) {
    private var initialized: Boolean = false
    private val engine = Engine(stateSource, trie, inputConnection)

    fun init() {
        trie.load()
        stateSource.update { copy(
            initialized = true,
            keypadEventSink = ::onUiEvent
        ) }
        initialized = true
    }
    fun onUiEvent(ev: KeypadEvent) {
        when (ev) {
            is KeypadEvent.KeyPress -> onKeyPress(ev.action)
            KeypadEvent.CloseRequest -> stateSource.update { copy(closed = true) }
            is KeypadEvent.CandidateSelect -> log.info("TODO onUiEvent($ev)")
        }
    }

    private fun onKeyPress(action: Action) {
        log.debug("type: ${action.symbol}")
        engine.type(action)
    }

    fun ensureInitialized() {
        if (!initialized) throw Uninitialized()
    }
    companion object {
        private val log = Logger.tag("Backend")
    }
    class Uninitialized : Throwable()

}
