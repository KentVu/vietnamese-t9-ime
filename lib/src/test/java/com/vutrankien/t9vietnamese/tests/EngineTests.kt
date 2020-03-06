package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.engine.T9Engine
import io.kotlintest.IsolationMode
import io.kotlintest.assertSoftly
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import kentvu.dawgjava.Trie
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.toList
import java.io.ByteArrayInputStream
import java.io.InputStream
import javax.inject.Inject

class EngineTests: AnnotationSpec() {
    override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest
    private lateinit var engine: T9Engine
    private lateinit var log: LogGenerator.Log

    private val padConfig = PadConfiguration(
            mapOf(
                    Key.num1 to KeyConfig(KeyType.Normal, linkedSetOf('a')),
                    Key.num2 to KeyConfig(KeyType.Normal, linkedSetOf('b')),
                    Key.num3 to KeyConfig(KeyType.Normal, linkedSetOf('c')),
                    Key.num0 to KeyConfig(KeyType.Confirm)
            )
    )

    @Before
    fun setUp() {
        val engineComponents = DaggerEngineComponents.builder().build()
        engine = engineComponents.engine()
        log = engineComponents.lg.newLog("EngineTests")
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

    //@Test
    fun `engineInitializing`() = runBlocking {
        engine.initialized shouldBe false
        launch {
            engine.init(emptySequence())
        }
        engine.eventSource.receive() shouldBe T9Engine.Event.Initialized
        engine.initialized shouldBe true
    }

    @Test
    fun engineInitializingWithProgress() = runBlocking {
        //withTimeout(1000) {
            engine.initialized shouldBe false
            launch(Dispatchers.Default) {
                engine.init("a\nb\nc".lineSequence())
            }
            engine.eventSource.receive() shouldBe T9Engine.Event.Initialized
            engine.initialized shouldBe true
        //}
    }

    @Test
    fun engineFunction1() = runBlocking {
        engineFunction(
            "a\nb\nc", padConfig,
            arrayOf(Key.num1, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("a")),
                T9Engine.Event.Confirm)
        )
    }

    @Test
    fun engineFunction2() = runBlocking {
        engineFunction(
            "a\nb\nc", padConfig,
            arrayOf(Key.num2, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("b")),
                T9Engine.Event.Confirm
            )
        )
    }

    private suspend fun engineFunction(
        seeds: String,
        padConfig: PadConfiguration,
        sequence: Array<Key>,
        expectedEvent: Array<T9Engine.Event>
    ) = withTimeout(100) {
        GlobalScope.launch {
            engine.init(seeds.lineSequence())
        }
        engine.pad = padConfig
        engine.eventSource.receive() shouldBe T9Engine.Event.Initialized

        GlobalScope.launch {
            sequence.forEach { engine.push(it) }
        }

        assertSoftly {
            expectedEvent.forEach {
                val event = engine.eventSource.receive()
                log.d("receive evt: $event")
                event shouldBe it
            }
        }
    }
}