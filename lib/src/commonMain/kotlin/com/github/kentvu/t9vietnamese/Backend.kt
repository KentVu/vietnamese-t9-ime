package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.Engine
import com.github.kentvu.t9vietnamese.model.T9AppEvent
import com.github.kentvu.t9vietnamese.model.WordList
import io.github.aakira.napier.Napier
import kotlinx.coroutines.launch
import okio.FileSystem

class Backend(private val ui: UI, wordlist: WordList, fileSystem: FileSystem) {
    private val engine = Engine(wordlist, fileSystem)

    fun init() {
        engine.init()
        ui.subscribeEvents { ev ->
            when(ev) {
                is UIEvent.KeyPress -> {
                    Napier.d("type: ${ev.key.symbol}")
                }
            }
        }
        ui.update(UI.UpdateEvent.Initialized)
    }

}
