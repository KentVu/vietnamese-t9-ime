package com.github.kentvu.t9vietnamese

class Sequencer(val listener: SequencerListener) {
    private val sequence = mutableListOf<Char>()
    fun input(c: Char) {
        sequence.add(c)
        listener.output(sequence.joinToString(""))
    }

    interface SequencerListener {
        fun output(s: String)
    }
}