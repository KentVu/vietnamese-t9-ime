package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.engine.T9EngineFactory
import com.vutrankien.t9vietnamese.trie.Trie
import com.vutrankien.t9vietnamese.trie.TrieFactory
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.io.InputStream

class EngineTests: AnnotationSpec() {
    private lateinit var trie: Trie

    @Before
    fun setUp() {
        trie = TrieFactory.newTrie()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun progressiveReaderTest() = runBlocking {
        val content = "a\nb\nc"
        val bytes = content.toByteArray()
        val inputStream = CheckableInputStream(ByteArrayInputStream(bytes))
        val progresses = inputStream.progressiveRead(this@runBlocking).toList()
        inputStream.closed shouldBe true
        progresses[0] shouldBe Progress(2, "a")
        progresses[1] shouldBe Progress(4, "b")
        progresses[2] shouldBe Progress(6, "c") // null terminating?
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

    private val padConfig = PadConfiguration(
            mapOf(
                Key.num1 to KeyConfig(KeyType.Normal, linkedSetOf('a')),
                Key.num2 to KeyConfig(KeyType.Normal, linkedSetOf('b')),
                Key.num3 to KeyConfig(KeyType.Normal, linkedSetOf('c')),
                Key.num0 to KeyConfig(KeyType.Confirm)
            )
    )

    @Test
    fun engineInitializing() = runBlocking {
        val engine: T9Engine = T9EngineFactory.newEngine(padConfig)
        engine.initialized shouldBe false
        engine.init(emptySequence())
        engine.initialized shouldBe true
    }

    @Test
    fun engineFunction1() = runBlocking {
        engineFunction(
            "a\nb\nc", padConfig, arrayOf(Key.num1,
                    Key.num0), "a"
        )
    }

    @Test
    fun engineFunction2() = runBlocking {
        engineFunction(
            "a\nb\nc", padConfig, arrayOf(Key.num2,
                    Key.num0), "b"
        )
    }

    private suspend fun engineFunction(
        seeds: String,
        padConfig: PadConfiguration,
        sequence: Array<Key>,
        expected: String
    ) {
        val engine: T9Engine = T9EngineFactory.newEngine(padConfig)
        engine.init(seeds.lineSequence())
        sequence.forEach { engine.push(it) }
        engine.candidates shouldBe expected
    }
}