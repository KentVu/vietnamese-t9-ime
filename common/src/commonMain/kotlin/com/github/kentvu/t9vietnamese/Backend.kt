package com.github.kentvu.t9vietnamese

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.Presenter.Companion.update
import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.Action
import com.github.kentvu.t9vietnamese.model.Trie

class Backend(
    private val trie: Trie,
    private val presenter: Presenter,
) {
    private var initialized: Boolean = false
    private val engine = Engine(presenter, trie)

    suspend fun init() {
        try {
            trie.load()
            presenter.update { copy(
                initialized = true,
                keypadEventSink = ::onUiEvent
            ) }
            initialized = true
        } catch (e: Exception) {
            presenter.update { copy(error = Exception("Cannot load trie!", e)) }
        }
    }
    fun onUiEvent(ev: KeypadEvent) {
        when (ev) {
            is KeypadEvent.KeyPress -> onKeyPress(ev.action)
            KeypadEvent.CloseRequest -> presenter.update { copy(closed = true) }
            is KeypadEvent.CandidateSelect -> engine.selectCandidate(ev.candidateId)
            is KeypadEvent.InputViewStart -> engine.switchMode(ev.editorInfo)
        }
    }

    private fun onKeyPress(action: Action) {
        log.debug("type: ${action.displaySymbol}")
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
