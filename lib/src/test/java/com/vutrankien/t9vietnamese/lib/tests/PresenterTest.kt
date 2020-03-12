package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.engine.T9Engine
import com.vutrankien.t9vietnamese.lib.*
import io.kotlintest.IsolationMode
import io.kotlintest.TestCase
import io.kotlintest.specs.FunSpec
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel

class PresenterTest: FunSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    private val seed: Sequence<String> = "a\nb\nc".lineSequence()
    private lateinit var view: View
    private lateinit var engine: T9Engine

    //val logGenerator = daggerComponents.logGenerator()
    fun getPresenter(): Presenter {
        return DaggerPresenterComponents.builder()
            .presenterModule(
                PresenterModule(
                    seed,
                    engine
                )
            )
            .build()
            .presenter()
    }

    override fun beforeTest(testCase: TestCase) {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        engine = mockk(relaxUnitFun = true)
        every { engine.eventSource } returns Channel()
    }

    init {
        test("showProgressIndicatorOnStart") {
            getPresenter().attachView(view)
            view.eventSource.send(Event.START.noData())
            verify(timeout = 100) { view.showProgress(any()) }
        }

        test("initializeEngineOnStart") {
            getPresenter().run {
                attachView(view)
            }
            view.eventSource.send(Event.START.noData())
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
            verify { view.confirmInput("4") }
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

