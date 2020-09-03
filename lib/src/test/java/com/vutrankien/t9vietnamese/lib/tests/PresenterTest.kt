package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.mockk.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel

class PresenterTest: FunSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val seed: Sequence<String> = "a\nb\nc".lineSequence()
    private lateinit var view: View
    private lateinit var engine: T9Engine
    private lateinit var env: Env

    override fun beforeTest(testCase: TestCase) {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        env = mockk()
        //every { env.... } returns ...

        engine = mockk(relaxUnitFun = true)
        every { engine.eventSource } returns Channel()
        every { engine.canReuseDb() } returns false
    }

    //val logGenerator = daggerComponents.logGenerator()
    private fun getPresenter(): Presenter {
        return DaggerPresenterComponents.builder()
            .presenterModule(
                PresenterModule(
                    seed,
                    engine,
                    env
                )
            )
            .build()
            .presenter()
    }

    init {
        test("showProgressIndicatorOnStart") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            verify(timeout = 100) { view.showProgress(any()) }
        }

        test("initializeEngineOnStart") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            coVerify { engine.init(seed) }
        }

        test("6.ReuseBuiltDawg") {
            every { engine.canReuseDb() } returns true
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            coVerify { engine.initFromDb() }
        }

        test("showKeyboardWhenEngineLoadCompleted") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            verify(timeout = 100) { view.showKeyboard() }
        }

        test("whenTypeOneNumberThenDisplayResult") {
            getPresenter().attachView(view)
            val cand = setOf("4")
            setupEngine(mapOf(Key.num0 to T9Engine.Event.Confirm("4"))) {T9Engine.Event.NewCandidates(cand)}
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify(timeout = 10) { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify(timeout = 1000) { view.showCandidates(cand) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify(timeout = 100) { view.confirmInput("4") }
        }

        test("Select next candidate") {
            getPresenter().attachView(view)
            val candidates = setOf("5", "6")
            setupEngine(
                mapOf(Key.num0 to T9Engine.Event.Confirm("5"),
                    Key.num1 to T9Engine.Event.NextCandidate),
                {T9Engine.Event.NewCandidates(candidates)})

            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify(timeout = 10) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify(timeout = 1000) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num1))
            verify { view.nextCandidate() }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify { view.confirmInput("5") }
        }

        test("Confirm input") {
            getPresenter().attachView(view)
            val candidates = setOf("5")
            setupEngine(mapOf(Key.num0 to T9Engine.Event.Confirm("5")),
                {T9Engine.Event.NewCandidates(candidates)})

            view.eventSource.send(Event.KEY_PRESS.withData(Key.num4))
            verify(timeout = 10) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num2))
            verify(timeout = 1000) { view.showCandidates(candidates) }
            view.eventSource.send(Event.KEY_PRESS.withData(Key.num0))
            verify { view.confirmInput("5") }
        }
    }

    private fun setupEngine(config: Map<Key, T9Engine.Event>, fallback: () -> T9Engine.Event) {
        coEvery {
            engine.push(any())
        } coAnswers {
            engine.eventSource.send(config[firstArg()] ?: fallback())
        }
    }
}

