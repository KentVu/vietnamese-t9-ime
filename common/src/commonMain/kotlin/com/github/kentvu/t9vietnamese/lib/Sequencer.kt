package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class Sequencer(keyEvents: Flow<Key>) {

    abstract val output: Flow<KeySequence>

    class DefaultSequencer(keyEvents: SharedFlow<Key>, scope: CoroutineScope): Sequencer(keyEvents) {
        private val sequence = mutableListOf<Key>()

        private val _output = MutableSharedFlow<KeySequence>()
        override val output: SharedFlow<KeySequence> = _output
        init {
            scope.launch {
                keyEvents.collect {
                    sequence.add(it)
                    _output.emit(KeySequence(sequence))
                }
            }
        }
    }

}