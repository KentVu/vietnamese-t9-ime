package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.ByteArrayInputStream
import java.io.InputStream
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

@OptIn(ExperimentalTime::class)
class EngineTests: FunSpec() {
    //override fun isolationMode(): IsolationMode? = IsolationMode.InstancePerTest

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
            Key.star to KeyConfig(
                KeyType.NextCandidate
            ),
            Key.left to KeyConfig(
                KeyType.PrevCandidate
            ),
            Key.num0 to KeyConfig(
                KeyType.Confirm
            )
        )
    )

    //private lateinit var engine: T9Engine
    private val envComponent: EnvComponent = DaggerEnvComponent.builder()
        //.logModule(ConfigurationModule(VnPad))
        .build()

    private val log: LogFactory.Log = envComponent.lg.newLog("EngineTests")

    private fun prepareEngine(pad: PadConfiguration): T9Engine {
        //engine = EngineComponent.Builder()
        val engine: T9Engine
        engine = envComponent.engineComponentBuilder
            .configurationModule(ConfigurationModule(pad)).build().engine()
        return engine
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
            prepareEngine(VnPad).apply {
                initialized shouldBe false
                seed()
                initialized shouldBe true
            }
        }

        test("engineInitializingWithProgress") {
            prepareEngine(VnPad).apply {
                initialized shouldBe false
                seed("a\nb\nc".lineSequence())
                initialized shouldBe true
            }
        }

        xtest("TODO:Engine should reset after Confirm") {
            prepareEngine(padConfig).seed("a\nb\nc".lineSequence())
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

        //context("f:engineFunction_2keys") {
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
        //}

        test("engineFunction_stdconfig_2keys").config(timeout = 180.seconds) {
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

        test("5.2.engineFunction_noCandidates").config(timeout = 180.seconds) {
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

        test("engineFunction_Vietnamese") {
            engineFunction(
                """
                                aa
                                ác
                                ắc
                                ách
                                bá
                                """.trimIndent().sortedSequence(),
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

        context("SelectCandidate") {
            val seeds = """
                                aa
                                ab
                                ac
                                ad
                                bd
                                ce
                                cf
                                """.trimIndent().sortedSequence()

            test("engineFunction_SelectCandidate") {
                engineFunction(
                    seeds,
                    padConfigStd,
                    arrayOf(Key.num1, Key.num1, Key.star, Key.num0),
                    arrayOf(
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                        T9Engine.Event.SelectCandidate(1),
                        T9Engine.Event.Confirm("ab")
                    )
                )
            }

            test("engineFunction_SelectCandidate2") {
                engineFunction(
                    seeds,
                    padConfigStd,
                    arrayOf(Key.num1, Key.num1, Key.star, Key.num0,
                        Key.num1, Key.num1, Key.star, Key.num0),
                    arrayOf(
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                        T9Engine.Event.SelectCandidate(1),
                        T9Engine.Event.Confirm("ab"),
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                        T9Engine.Event.SelectCandidate(1),
                        T9Engine.Event.Confirm("ab")
                    )
                )
            }

            test("engineFunction_SelectPrevCandidate") {
                engineFunction(
                    seeds,
                    padConfigStd,
                    arrayOf(Key.num1, Key.num1, Key.star, Key.left, Key.num0),
                    arrayOf(
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                        T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                        T9Engine.Event.SelectCandidate(1),
                        T9Engine.Event.SelectCandidate(0),
                        T9Engine.Event.Confirm("aa")
                    )
                )
            }
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

    private suspend fun T9Engine.seed(sequence: Sequence<String> = emptySequence()): Unit = coroutineScope {
        launch {
            init(sequence)
        }
        for (event in eventSource)
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
        expectedEvents: Array<T9Engine.Event>
    ):Unit = coroutineScope<Unit> {
        prepareEngine(padConfig).apply {
            seed(seeds)
            launch {
                sequence.forEach { push(it) }
            }
            assertSoftly {
                expectedEvents.forEach { expectedEvt ->
                    val event = eventSource.receive()
                    log.d("receive evt: $event")
                    if (event is T9Engine.Event.NewCandidates) {
                        assertTrue(expectedEvt is T9Engine.Event.NewCandidates, "evt($event) is not expected exp($expectedEvt)")
                        //event should containAll (expectedEvt as T9Engine.Event.NewCandidates)
                        assertTrue(event.contains(expectedEvt as T9Engine.Event.NewCandidates))
                    } else {
                        event shouldBe expectedEvt
                    }
                }
            }
        }
    }
}

private fun String.sortedSequence(): Sequence<String> {
    return lineSequence().toSortedSet().asSequence()
}
