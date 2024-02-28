package com.github.kentvu.t9vietnamese

import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.Trie

class Backend(private val ui: UI, private val trie: Trie) {
    private var initialized: Boolean = false
    private val engine = Engine(ui, trie)
    //private val fullSequence = StringBuilder(10)

    fun init() {
        trie.load()
        ui.subscribeKeypadEvents { ev ->
            when (ev) {
                is KeypadEvent.KeyPress -> onKeyPress(ev.key)
                KeypadEvent.CloseRequest -> ui.update(UI.UpdateEvent.Close)
            }
        }
        ui.update(UI.UpdateEvent.Initialized)
        initialized = true
    }

    private fun onKeyPress(key: Key) {
        Logger.tag("Backend").debug("type: ${key.symbol}")
        engine.type(key)
    }

    fun ensureInitialized() {
        if (!initialized) throw Uninitialized()
    }

    class Uninitialized : Throwable()

}
