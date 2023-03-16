package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.VNKeys
import io.github.aakira.napier.Napier

class Backend(private val ui: UI, private val trie: Trie) {
    private var initialized: Boolean = false
    private val engine = Engine(ui, trie)
    //private val fullSequence = StringBuilder(10)

    fun init() {
        trie.load()
        ui.subscribeEvents { ev ->
            when (ev) {
                is UIEvent.KeyPress -> onKeyPress(ev.key)
                UIEvent.CloseRequest -> ui.update(UI.UpdateEvent.Close)
            }
        }
        ui.update(UI.UpdateEvent.Initialized)
        initialized = true
    }

    private fun onKeyPress(key: Key) {
        Napier.d("type: ${key.symbol}")
        engine.type(key)
    }

    fun ensureInitialized() {
        if (!initialized) throw Uninitialized()
    }

    class Uninitialized : Throwable()

}
