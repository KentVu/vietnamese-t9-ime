package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.View
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow

abstract class T9Engine(input: Flow<KeySequence>) {

    abstract val output: Flow<T9EngineOutput>

    data class T9EngineOutput(val candidates: Set<String>) {
        init {
            //candidates.forEach
        }
    }
}
