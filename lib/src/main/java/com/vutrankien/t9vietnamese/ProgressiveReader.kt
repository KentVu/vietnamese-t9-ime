package com.vutrankien.t9vietnamese

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.InputStream

/**
 * @param size estimated size of this stream, in bytes.
 */
@ExperimentalCoroutinesApi
fun InputStream.start(size: Int, scope: CoroutineScope = GlobalScope): ReceiveChannel<Progress> = scope.produce {
    TODO()
}

data class Progress(val bytesRead: Int, val line: String)
