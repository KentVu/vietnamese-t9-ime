package com.github.kentvu.t9vietnamese

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

abstract class T9Engine {

    val output: Output

    class Output: ReceiveChannel<T9EngineOutput> by (Channel())

    data class T9EngineOutput(val candidates: Set<String>) {

    }
}
