package com.github.kentvu.t9vietnamese

class Sequencer(val listener: SequencerListener) {
    fun input(c: Char) {
        TODO("Not yet implemented")
    }

    interface SequencerListener {
        fun output(s: String)

    }
}