package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.VNKeys
import com.github.kentvu.t9vietnamese.model.WordList
import io.github.aakira.napier.Napier
import okio.FileSystem

class Backend(private val ui: UI, wordlist: WordList, fileSystem: FileSystem) {
    private var initialized: Boolean = false
    private val engine = Engine(wordlist, fileSystem)

    fun init() {
        engine.init()
        ui.subscribeEvents { ev ->
            when(ev) {
                is UIEvent.KeyPress -> onKeyPress(ev.key)
                UIEvent.CloseRequest -> ui.update(UI.UpdateEvent.Close)
            }
        }
        ui.update(UI.UpdateEvent.Initialized)
        initialized = true
    }

    private fun onKeyPress(key: Key) {
        Napier.d("type: ${key.symbol}")
        if (key == VNKeys.keyStar) {
            ui.update(UI.UpdateEvent.SelectNextCandidate)
        } else if (key == VNKeys.key0) {
            ui.update(UI.UpdateEvent.Confirm)
        } else {
            engine.type(key)
            ui.update(UI.UpdateEvent.NewCandidates(engine.candidates))
        }
    }

    fun ensureInitialized() {
        if(!initialized) throw Uninitialized()
    }

    class Uninitialized : Throwable()

}
