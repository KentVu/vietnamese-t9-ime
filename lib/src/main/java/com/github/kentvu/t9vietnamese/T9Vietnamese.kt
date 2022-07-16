package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.jvm.DummyView
import com.github.kentvu.t9vietnamese.model.KeyPad

class T9Vietnamese(private val keyPad: KeyPad) {
    private val view: DummyView

    init {
        view = DummyView(
            DawgT9Engine(
                Sequencer.DefaultSequencer(keyPad.output).output
            ).output
        )
    }
}
