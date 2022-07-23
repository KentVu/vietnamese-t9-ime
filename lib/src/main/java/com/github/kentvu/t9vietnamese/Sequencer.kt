package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

abstract class Sequencer(keyEvents: Flow<Key>) {

    abstract val output: Flow<KeySequence>

    class DefaultSequencer(keyEvents: Flow<Key>): Sequencer(keyEvents) {
        private val sequence = mutableListOf<Key>()

        override val output: Flow<KeySequence> =
            keyEvents.transform<Key,KeySequence> {
                sequence.add(it)
                emit(KeySequence(sequence))
            }
    }

}
