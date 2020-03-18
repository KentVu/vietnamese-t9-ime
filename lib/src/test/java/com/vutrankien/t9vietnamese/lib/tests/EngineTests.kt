package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotlintest.*
import io.kotlintest.specs.FunSpec
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.toList
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.Duration

class EngineTests: FunSpec() {
    override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest
    private lateinit var engine: T9Engine
    private lateinit var log: LogFactory.Log

    private val padConfig = PadConfiguration(
        mapOf(
            Key.num1 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('a')
            ),
            Key.num2 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('b')
            ),
            Key.num3 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('c')
            ),
            Key.num0 to KeyConfig(
                KeyType.Confirm
            )
        )
    )
    private val padConfigStd = PadConfiguration(
        mapOf(
            Key.num1 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('a', 'b', 'c')
            ),
            Key.num2 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('d', 'e', 'f')
            ),
            Key.num3 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('g', 'h', 'i')
            ),
            Key.num0 to KeyConfig(
                KeyType.Confirm
            )
        )
    )

    override fun beforeTest(testCase: TestCase) {
        val engineComponents = DaggerEngineComponents.builder().build()
        engine = engineComponents.engine()
        log = engineComponents.lg.newLog("EngineTests")
    }

    /**
     * assert inputStream has closed?
     */
    class CheckableInputStream(val delegated: InputStream) : InputStream() {
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

    init {
        test("progressiveReaderTest") {
            val content = "a\nb\nc"
            val bytes = content.toByteArray()
            val inputStream =
                CheckableInputStream(
                    ByteArrayInputStream(bytes)
                )
            val progresses = inputStream.progressiveRead(this).toList()
            inputStream.closed shouldBe true
            progresses[0] shouldBe Progress(2, "a")
            progresses[1] shouldBe Progress(4, "b")
            progresses[2] shouldBe Progress(
                6,
                "c"
            ) // null terminating?
        }

        test("engineInitializing") {
            engine.initialized shouldBe false
            seedEngine()
            engine.initialized shouldBe true
        }

        test("engineInitializingWithProgress") {
            engine.initialized shouldBe false
            seedEngine("a\nb\nc".lineSequence())
            engine.initialized shouldBe true
        }

        test("engineFunction_1key_1") {
            engineFunction(
                "a\nb\nc".lineSequence(), padConfig,
                arrayOf(Key.num1, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("a")),
                    T9Engine.Event.Confirm("a")
                )
            )
        }

        test("engineFunction_1key_2") {
            engineFunction(
                """
                                a
                                aa
                                ab
                                ac
                                """.trimIndent().lineSequence(),
                padConfig,
                arrayOf(Key.num1, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("a", "ab", "ac", "aa")),
                    T9Engine.Event.Confirm("a")
                )
            )
        }

        test("engineFunction_2keys") {
            engineFunction(
                """
                                aa
                                ab
                                ac
                                ba
                                """.trimIndent().lineSequence(),
                padConfig,
                arrayOf(Key.num1, Key.num1, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("ab", "ac", "aa")),
                    T9Engine.Event.NewCandidates(setOf("aa")),
                    T9Engine.Event.Confirm("aa")
                )
            )
        }

        test("engineFunction_stdconfig_2keys").config(timeout = Duration.ofSeconds(180)) {
            engineFunction(
                """
                                aa
                                ab
                                ac
                                ad
                                bd
                                ce
                                cf
                                """.trimIndent().lineSequence(),
                padConfigStd,
                arrayOf(Key.num1, Key.num2, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "ac", "ad", "bd")),
                    T9Engine.Event.NewCandidates(setOf("ad", "bd", "ce", "cf")),
                    T9Engine.Event.Confirm("ad")
                )
            )
        }

        test("5.2.engineFunction_noCandidates").config(timeout = Duration.ofSeconds(180)) {
            engineFunction(
                emptySequence(),
                padConfig,
                arrayOf(Key.num1, Key.num2, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(emptySet()),
                    T9Engine.Event.NewCandidates(emptySet()),
                    T9Engine.Event.Confirm("12")
                )
            )
        }

        test("5.3.engineFunction_noCandidates_2keys") {
            engineFunction(
                """
                                aa
                                ab
                                ba
                                """.trimIndent().lineSequence(),
                padConfig,
                arrayOf(Key.num1, Key.num3, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                    T9Engine.Event.NewCandidates(setOf("13")),
                    T9Engine.Event.Confirm("13")
                )
            )
        }

        test("f:engineFunction_Vietnamese") {
            engineFunction(
                """
                                ác
                                ắc
                                ách
                                bá
                                """.trimIndent().lineSequence(),
                vnPad,
                arrayOf(Key.num1, Key.num1, Key.num3, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("ác", "ách", "1")),
                    T9Engine.Event.NewCandidates(setOf("ác", "ách", "11")),
                    T9Engine.Event.NewCandidates(setOf("ác", "ách", "113")),
                    T9Engine.Event.Confirm("ác")
                )
            )
        }
    }

    private val vnPad = PadConfiguration(
        mapOf(
            Key.num1 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('a', '́')
            ),
            Key.num2 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('b', 'ă')
            ),
            Key.num3 to KeyConfig(
                KeyType.Normal,
                linkedSetOf('c')
            ),
            Key.num0 to KeyConfig(
                KeyType.Confirm
            )
        )
    )

    private suspend fun seedEngine(sequence: Sequence<String> = emptySequence()): Unit = coroutineScope {
        launch {
            engine.init(sequence)
        }
        for (event in engine.eventSource)
            if (event is T9Engine.Event.LoadProgress)
                log.v("Engine.LoadProgress ${event.bytes}")
            else if (event is T9Engine.Event.Initialized) {
                log.v("Engine.Initialized!")
                break
            }
    }

    private suspend fun engineFunction(
        seeds: Sequence<String>,
        padConfig: PadConfiguration,
        sequence: Array<Key>,
        expectedEvent: Array<T9Engine.Event>
    ):Unit = coroutineScope {
        engine.pad = padConfig
        seedEngine(seeds)
        launch {
            sequence.forEach { engine.push(it) }
        }
        assertSoftly {
            expectedEvent.forEach { expectedEvt ->
                val event = engine.eventSource.receive()
                log.d("receive evt: $event")
                if (event is T9Engine.Event.NewCandidates) {
                    //event should containAll (expectedEvt as T9Engine.Event.NewCandidates)
                    assertTrue(event.contains(expectedEvt as T9Engine.Event.NewCandidates))
                } else {
                    event shouldBe expectedEvt
                }
            }
        }
    }
}