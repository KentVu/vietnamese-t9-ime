package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Action
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class Sequencer(keyEvents: Flow<Action>) {

    abstract val output: Flow<ActionSequence>

    class DefaultSequencer(keyEvents: SharedFlow<Action>, scope: CoroutineScope): Sequencer(keyEvents) {
        private val sequence = mutableListOf<Action>()

        private val _output = MutableSharedFlow<ActionSequence>()
        override val output: SharedFlow<ActionSequence> = _output
        init {
            scope.launch {
                keyEvents.collect {
                    sequence.add(it)
                    _output.emit(ActionSequence(sequence))
                }
            }
        }
    }

}