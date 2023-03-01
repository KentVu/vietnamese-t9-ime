package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.WordList
import io.github.aakira.napier.Napier
import okio.FileSystem

class Backend(private val ui: UI, wordlist: WordList, fileSystem: FileSystem) {
    private val engine = Engine(wordlist, fileSystem)

    fun init() {
        engine.init()
        ui.subscribeEvents { ev ->
            when(ev) {
                is UIEvent.KeyPress -> {
                    onKeyPress(ev.key)
                }
            }
        }
        ui.update(UI.UpdateEvent.Initialized)
    }

    private fun onKeyPress(key: Key) {
        Napier.d("type: ${key.symbol}")
        engine.type(key)
        ui.update(UI.UpdateEvent.NewCandidates(engine.candidates))
    }

}
