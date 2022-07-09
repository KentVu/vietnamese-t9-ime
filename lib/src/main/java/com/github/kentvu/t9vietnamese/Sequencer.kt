package com.github.kentvu.t9vietnamese

interface Sequencer {
    fun input(key: Char)

    interface SequencerListener {
        fun output(s: String)
    }

    fun listen(l: SequencerListener)

    class DefaultSequencer(): Sequencer {
        private val sequence = mutableListOf<Char>()
        private var listener: SequencerListener? = null

        override fun input(c: Char) {
            sequence.add(c)
            listener?.output(sequence.joinToString(""))
        }

        override fun listen(l: SequencerListener) {
            listener = l
        }
    }

}
