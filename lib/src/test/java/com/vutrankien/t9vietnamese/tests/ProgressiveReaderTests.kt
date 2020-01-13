package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.Progress
import com.vutrankien.t9vietnamese.start
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class ProgressiveReaderTests {
    @ExperimentalCoroutinesApi
    @Test
    fun basicFunction() = runBlocking {
        val content = "a\nb\nc"
        val bytes = content.toByteArray()
        val inputStream = CheckableInputStream(ByteArrayInputStream(bytes))
        val progresses = inputStream.start(this@runBlocking).toList()
        assertTrue(inputStream.closed)
        assertEquals(progresses[0], Progress(2, "a"))
        assertEquals(progresses[1], Progress(4, "b"))
        assertEquals(progresses[2], Progress(6, "c"))
    }

    /**
     * assert inputStream has closed?
     */
    class CheckableInputStream(val delegated: InputStream): InputStream() {
        var closed = false
            private set

        override fun read(): Int =
                delegated.read()

        override fun close() {
            delegated.close()
            super.close()
            closed = true
        }
    }
}