package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Trie
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class DawgT9Engine(val trie: Trie, input: SharedFlow<KeySequence>, scope: CoroutineScope) : T9Engine(input) {
    private val _output = MutableSharedFlow<T9EngineOutput>()
    override val output: Flow<T9EngineOutput> = _output.asSharedFlow()
    init {
        scope.launch {
            input.collect {
                _output.emit(T9EngineOutput(setOf(it.asString())))
            }
        }
    }
}