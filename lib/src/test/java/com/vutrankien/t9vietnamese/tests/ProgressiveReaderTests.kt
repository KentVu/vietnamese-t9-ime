package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.ProgressiveReader
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream

class ProgressiveReaderTests {
    @ExperimentalCoroutinesApi
    @Test
    fun basicFunction() = runBlocking {
        val content = "a\nb\nc"
        val bytes = content.toByteArray()
        val progresses = ProgressiveReader(
            ByteArrayInputStream(bytes),
            bytes.size
        ).start(this@runBlocking).toList()
        assertEquals(progresses[0], ProgressiveReader.Progress(2, "a"))
        assertEquals(progresses[1], ProgressiveReader.Progress(4, "b"))
        assertEquals(progresses[2], ProgressiveReader.Progress(5, "c"))
    }
}