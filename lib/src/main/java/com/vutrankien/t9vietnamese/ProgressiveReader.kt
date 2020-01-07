package com.vutrankien.t9vietnamese

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.toChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import java.io.InputStream

class ProgressiveReader(inputStream: InputStream, size: Int) {
    @ExperimentalCoroutinesApi
    fun start(scope: CoroutineScope = GlobalScope): ReceiveChannel<Progress> = scope.produce {
        TODO()
    }

    data class Progress(val bytesRead: Int, val line: String)
}
