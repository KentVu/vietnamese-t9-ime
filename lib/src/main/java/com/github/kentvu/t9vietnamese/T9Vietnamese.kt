package com.github.kentvu.t9vietnamese

class T9Vietnamese() {
    private val view: DummyView

    init {
        view = DummyView(
            DawgT9Engine(
                Sequencer.DefaultSequencer(
                    DefaultKeyPad().keyEvents
                ).output
            ).output
        )
    }
}
