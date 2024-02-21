package com.github.kentvu.t9vietnamese.lib

import kotlinx.coroutines.flow.Flow

abstract class T9Engine(input: Flow<KeySequence>) {

    abstract val output: Flow<T9EngineOutput>

    data class T9EngineOutput(val candidates: Set<String>) {
        init {
            //candidates.forEach
        }
    }
}