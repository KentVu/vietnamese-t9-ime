package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.KeyPadOutput

abstract class Sequencer {
    abstract fun input(key: Char)

    interface Output {
    }

    abstract val output: Output

    class DefaultSequencer(private val input: KeyPadOutput): Sequencer {
        private val sequence = mutableListOf<Char>()

        override fun input(c: Char) {
            sequence.add(c)
            listener?.output(sequence.joinToString(""))
        }
    }

}
