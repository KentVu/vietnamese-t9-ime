package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

class PresenterTest: FunSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private lateinit var engine: T9Engine
    private lateinit var env: Env

    abstract class MockView : View {
        private val channel = Channel<EventWithData<Event, Key>>()
        internal val eventSink: SendChannel<EventWithData<Event, Key>>
            get() = channel
        override val eventSource: ReceiveChannel<EventWithData<Event, Key>>
            get() = channel
        override val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + Job())
    }
    private lateinit var view: MockView

    override fun beforeTest(testCase: TestCase) {
        view = spyk()

        env = mockk()
        //every { env.... } returns ...

        engine = mockk(relaxUnitFun = true)
        every { engine.eventSource } returns Channel()
        //every { engine.canReuseDb() } returns false
        view.scope.launch {
            presenter.start()
        }
    }

    private val presenter: Presenter by lazy { Presenter(JavaLogFactory, engine, view) }

    init {
//        context("Presenter") {

        test("showProgressIndicatorOnStart") {
            view.eventSink.send(Event.START.noData())
            verify(timeout = 100) { view.showProgress(any()) }
        }

        test("initializeEngineOnStart") {
            view.eventSink.send(Event.START.noData())
            coVerify { engine.init() }
        }

        test("6.ReuseBuiltDawg") {
            view.eventSink.send(Event.START.noData())
            coVerify { engine.init() }
        }

        test("showKeyboardWhenEngineLoadCompleted") {
            view.eventSink.send(Event.START.noData())
            verify(timeout = 100) { view.showKeyboard() }
        }

        test("whenTypeOneNumberThenDisplayResult") {
            val cand = setOf("4")
            setupEngine(mapOf(Key.Num0 to T9Engine.Event.Confirm("4"))) {T9Engine.Event.NewCandidates(cand)}
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num4))
            verify(timeout = 100) { view.showCandidates(cand) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num2))
            verify(timeout = 1000) { view.showCandidates(cand) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num0))
            verify(timeout = 100) { view.confirmInput("4") }
        }

        test("Select candidate") {
            val candidates = setOf("5", "6")
            val selectedCandidate = 1
            setupEngine(
                mapOf(Key.Num0 to T9Engine.Event.Confirm("5"),
                    Key.Num1 to T9Engine.Event.SelectCandidate(selectedCandidate)),
                {T9Engine.Event.NewCandidates(candidates)})

            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num4))
            verify(timeout = 10) { view.showCandidates(candidates) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num2))
            verify(timeout = 1000) { view.showCandidates(candidates) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num1))
            verify { view.candidateSelected(selectedCandidate) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num0))
            verify(timeout = 500) { view.confirmInput("5") }
        }

        test("Confirm input") {
            val candidates = setOf("5")
            setupEngine(mapOf(Key.Num0 to T9Engine.Event.Confirm("5")),
                {T9Engine.Event.NewCandidates(candidates)})

            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num4))
            verify(timeout = 10) { view.showCandidates(candidates) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num2))
            verify(timeout = 1000) { view.showCandidates(candidates) }
            view.eventSink.send(Event.KEY_PRESS.withData(Key.Num0))
            verify(timeout = 100) { view.confirmInput("5") }
        }
//        }
    }

    private fun setupEngine(config: Map<Key, T9Engine.Event>, fallback: () -> T9Engine.Event) {
        coEvery {
            engine.push(any())
        } coAnswers {
            engine.eventSource.send(config[firstArg()] ?: fallback())
        }
    }
}

