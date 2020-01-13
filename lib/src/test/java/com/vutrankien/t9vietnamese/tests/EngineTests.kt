package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

class EngineTests {
    @Before
    fun setUp() {
        //mock the wordlist!
    }

    @ExperimentalCoroutinesApi
    @Test
    fun progressiveReaderTest() = runBlocking {
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

    val padConfig = PadConfiguration(mapOf(
            Key.num1 to KeyConfig(KeyTypes.Normal, linkedSetOf('a')),
            Key.num2 to KeyConfig(KeyTypes.Normal, linkedSetOf('b')),
            Key.num3 to KeyConfig(KeyTypes.Normal, linkedSetOf('c'))))

    @Test
    fun engineInitializing() = runBlocking {
        val seeds = "a\nb\nc"
        val engine: T9Engine = DefaultT9Engine(seeds, padConfig)
        assertFalse(engine.initialized)
        engine.init().await()
        assertTrue(engine.initialized)
    }

    @Test
    fun engineFunction() = runBlocking {
        val seeds = "a\nb\nc"
        val engine: T9Engine = DefaultT9Engine(seeds, padConfig)
        engine.init().await()
        engine.startInput().input(Key.num1)
    }
}