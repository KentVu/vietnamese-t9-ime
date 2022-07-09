package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key

class DefaultKeyPad() : KeyPad {
    object SequencerListener: Sequencer.SequencerListener
    {
        override fun output(s: String) {
            println("SequencerListener:output:$s")
        }
    }

    private val sequencer: Sequencer = Sequencer.DefaultSequencer().apply { listen(SequencerListener) }

    override fun type(key: Key) {
        type(key.symbol)
    }

    private fun type(key: Char) {
        sequencer.input(key)
    }
}
