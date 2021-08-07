package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.DefaultT9Engine
import com.vutrankien.t9vietnamese.lib.Seed
import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotest.assertions.assertSoftly
import io.kotest.core.plan.Descriptor.EngineDescriptor.name
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.junit.jupiter.api.Assertions.assertTrue
import java.io.ByteArrayInputStream
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

suspend fun T9Engine.seed(log: LogFactory.Log): Unit = coroutineScope {
    launch {
        init()
    }
    for (event in eventSource)
        if (event is T9Engine.Event.LoadProgress)
            log.v("Engine.LoadProgress ${event.bytes}")
        else if (event is T9Engine.Event.Initialized) {
            log.v("Engine.Initialized!")
            break
        }
}

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

    private val lg: LogFactory = JavaLogFactory
    private val log: LogFactory.Log = lg.newLog("EngineTests")

    private fun prepareEngine(
        pad: PadConfiguration,
        seed: Seed = Seed.EmptySeed,
        env: Env = JvmEnv,
        dawgFile: String = "TestT9Engine.dawg",
        overwriteDawgFile: Boolean = true
    ): T9Engine {
        val engine: T9Engine
        engine = DefaultT9Engine(seed, pad, lg, TrieDb(lg, env, dawgFile, overwriteDawgFile))
        return engine
    }

    suspend fun T9Engine.seed() = seed(log)

    private fun Test.runTest() {
        test(name) {
            this@runTest.go()
        }
    }

    init {
        ProgressiveReaderTest().runTest()

        EngineInitializingTest(lg, prepareEngine(VnPad, env = NoFileEnv)).run {
            test(name).config(timeout = Duration.seconds(10)) { go() }
        }

        EngineInitializingTest(lg, prepareEngine(VnPad), "EngineLoadingTest").run {
            test(name) { go() }
        }

        EngineInitializingWithProgress(
            lg, prepareEngine(
                VnPad,
                MockSeed("a", "b", "c")
            )
        ).runTest()

        xtest("TODO:Engine should reset after Confirm") {
            prepareEngine(padConfig, MockSeed("a", "b", "c"))
        }

        EnginePushTest(
            lg,
            "engineFunction_1key_1",
            prepareEngine(
                padConfig,
                MockSeed("a", "b", "c")
            ),
            arrayOf(Key.num1, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("a")),
                T9Engine.Event.Confirm("a")
            )
        ).runTest()

        EnginePushTest(
            lg,
            "engineFunction_1key_2",
            prepareEngine(
                padConfig,
                MockSeed(
                    "a",
                    "aa",
                    "ab",
                    "ac"
                )
            ),
            arrayOf(Key.num1, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("a", "ab", "ac", "aa")),
                T9Engine.Event.Confirm("a")
            )
        ).runTest()

        //context("f:engineFunction_2keys") {
        EnginePushTest(
            lg,
            "engineFunction_2keys",
            prepareEngine(
                padConfig,
                MockSeed
                    (
                    "aa",
                    "ab",
                    "ac",
                    "ba"
                )
            ),
            arrayOf(Key.num1, Key.num1, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("ab", "ac", "aa")),
                T9Engine.Event.NewCandidates(setOf("aa")),
                T9Engine.Event.Confirm("aa")
            )
        ).runTest()
        //}

        EnginePushTest(
            lg, "engineFunction_stdconfig_2keys",
            prepareEngine(
                padConfigStd,
                MockSeed(
                    "aa",
                    "ab",
                    "ac",
                    "ad",
                    "bd",
                    "ce",
                    "cf"
                )
            ),
            arrayOf(Key.num1, Key.num2, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("aa", "ab", "ac", "ad", "bd")),
                T9Engine.Event.NewCandidates(setOf("ad", "bd", "ce", "cf")),
                T9Engine.Event.Confirm("ad")
            )
        ).run {
            test(name).config(timeout = (180).toDuration(TimeUnit.SECONDS)) { go() }
        }

        EnginePushTest(
            lg, "5.2.engineFunction_noCandidates",
            prepareEngine(
                padConfig,
                Seed.EmptySeed
            ),
            arrayOf(Key.num1, Key.num2, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(emptySet()),
                T9Engine.Event.NewCandidates(emptySet()),
                T9Engine.Event.Confirm("12")
            )
        ).run {
            test(name).config(timeout = Duration.seconds(180)) { go() }
        }

        EnginePushTest(
            lg, "5.3.engineFunction_noCandidates_2keys", prepareEngine(
                padConfig,
                MockSeed(
                    "aa",
                    "ab",
                    "ba"
                )
            ),
            arrayOf(Key.num1, Key.num3, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                T9Engine.Event.NewCandidates(setOf("13")),
                T9Engine.Event.Confirm("13")
            )
        ).runTest()

        EnginePushTest(
            lg, "engineFunction_Vietnamese",
            prepareEngine(
                vnPad,
                SortedSeed(
                    "aa",
                    "ác",
                    "ắc",
                    "ách",
                    "bá"
                )
            ),
            arrayOf(Key.num1, Key.num1, Key.num3, Key.num0),
            arrayOf(
                T9Engine.Event.NewCandidates(setOf("ác", "ách", "1")),
                T9Engine.Event.NewCandidates(setOf("ác", "ách", "11")),
                T9Engine.Event.NewCandidates(setOf("ác", "ách", "113")),
                T9Engine.Event.Confirm("ác")
            )
        ).run { test(name) { go() } }

        context("SelectCandidate") {
            val seeds = SortedSeed(
                "aa",
                "ab",
                "ac",
                "ad",
                "bd",
                "ce",
                "cf"
            )
            EnginePushTest(
                lg, "engineFunction_SelectCandidate",
                prepareEngine(
                    padConfigStd,
                    seeds
                ),
                arrayOf(Key.num1, Key.num1, Key.star, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                    T9Engine.Event.SelectCandidate(1),
                    T9Engine.Event.Confirm("ab")
                )
            ).run { test(name) { go() } }

            EnginePushTest(
                lg, "engineFunction_SelectCandidate2",
                prepareEngine(
                    padConfigStd,
                    seeds
                ),
                arrayOf(
                    Key.num1, Key.num1, Key.star, Key.num0,
                    Key.num1, Key.num1, Key.star, Key.num0
                ),
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
            ).run { test(name) { go() } }

            EnginePushTest(
                lg, "engineFunction_SelectPrevCandidate",
                prepareEngine(
                    padConfigStd,
                    seeds
                ),
                arrayOf(Key.num1, Key.num1, Key.star, Key.left, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "11")),
                    T9Engine.Event.SelectCandidate(1),
                    T9Engine.Event.SelectCandidate(0),
                    T9Engine.Event.Confirm("aa")
                )
            ).run { test(name) { go() } }
        }

        context("Defects") {
            val seeds = MockSeed(
                "aa",
                "ab",
                "ac",
                "ad",
                "bd",
                "ce",
                "cf"
            )
            EnginePushTest(
                lg, "Cannot navigate back anymore",
                prepareEngine(
                    padConfigStd,
                    seeds
                ),
                arrayOf(Key.num1, Key.star, Key.left, Key.left, Key.num0),
                arrayOf(
                    T9Engine.Event.NewCandidates(setOf("aa", "ab", "1")),
                    T9Engine.Event.SelectCandidate(1),
                    T9Engine.Event.SelectCandidate(0),
                    T9Engine.Event.SelectCandidate(0),
                    T9Engine.Event.Confirm("aa")
                )
            ).run { test(name) { go() } }

        }
    }

    class SortedSeed(private vararg val words: String) :
        Seed {
        override fun sequence(): Sequence<String> {
            return words.toSortedSet().asSequence()
        }

    }

    class MockSeed(private vararg val words: String) :
        Seed {
        override fun sequence(): Sequence<String> {
            return words.asSequence()
        }

    }

    class ProgressiveReaderTest() : Test {
        override val name: String = "progressiveReaderTest"

        @kotlinx.coroutines.ExperimentalCoroutinesApi
        override suspend fun go() = coroutineScope {
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

    }

    class EngineInitializingTest(val lg: LogFactory, private val engine: T9Engine,
                                 override val name: String = "engineInitializing"
    ): Test {

        override suspend fun go() {
            engine.apply {
                initialized shouldBe false
                seed(lg.newLog(name))
                initialized shouldBe true
            }
        }

    }

    class EngineInitializingWithProgress(val lg: LogFactory, private val engine: T9Engine): Test {
        override val name: String = "engineInitializingWithProgress"

        override suspend fun go() {
            engine.apply {
                initialized shouldBe false
                seed(lg.newLog("EngineInitializingWithProgress"))
                initialized shouldBe true
            }
        }

    }

    class EnginePushTest(
        lg: LogFactory,
        override val name: String,
        private val engine: T9Engine,
        private val sequence: Array<Key>,
        private val expectedEvents: Array<T9Engine.Event>
    ): Test {

        private val log: LogFactory.Log = lg.newLog("name")

        override suspend fun go() = coroutineScope<Unit> {
            engine.apply {
                seed(log)
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
}
