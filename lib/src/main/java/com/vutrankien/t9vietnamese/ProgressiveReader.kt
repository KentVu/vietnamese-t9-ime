package com.vutrankien.t9vietnamese

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.InputStream

// TODO: Stop estimating and add a parameter
const val ESTIMATED_LINEBREAK_SIZE = 1 // assume linux/mac line endings. (\r, \n only)

@ExperimentalCoroutinesApi
fun InputStream.start(scope: CoroutineScope = GlobalScope): ReceiveChannel<Progress> {
    return scope.produce {
        val bufferedReader = this@start.bufferedReader()
        bufferedReader.useLines {
            var bytesRead = 0
            it.forEach { line ->
                bytesRead += line.toByteArray().size + ESTIMATED_LINEBREAK_SIZE
                send(Progress(bytesRead, line))
            }
        }
    }
}

data class Progress(val bytesRead: Int, val line: String)
