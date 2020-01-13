package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.Progress
import com.vutrankien.t9vietnamese.start
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
        val progresses = ByteArrayInputStream(bytes).start(
                bytes.size,
                this@runBlocking).toList()
        assertEquals(progresses[0], Progress(2, "a"))
        assertEquals(progresses[1], Progress(4, "b"))
        assertEquals(progresses[2], Progress(5, "c"))
    }
}