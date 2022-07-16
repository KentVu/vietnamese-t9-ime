package com.github.kentvu.t9vietnamese

abstract class Sequencer {
    abstract fun input(key: Char)

    interface Output {
    }

    abstract val output: Output

    class DefaultSequencer(): Sequencer {
        private val sequence = mutableListOf<Char>()

        override fun input(c: Char) {
            sequence.add(c)
            listener?.output(sequence.joinToString(""))
        }
    }

}
