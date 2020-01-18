package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
import com.vutrankien.t9vietnamese.engine.T9Engine
import io.kotlintest.IsolationMode
import io.kotlintest.specs.AnnotationSpec
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class PresenterTest: AnnotationSpec() {
    override fun isolationMode(): IsolationMode = IsolationMode.InstancePerTest

    lateinit var view: View
    lateinit var engine: T9Engine

    @Before
    fun setUp() {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        engine = mockk(relaxUnitFun = true)
        coEvery { engine.init() } just Runs
    }

    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        withTimeout(1000) {
            Presenter(engine).attachView(view)
            view.eventSource.send(Event.START.noData())
            verify { view.showProgress() }
        }
    }

    @Suppress("DeferredResultUnused") // just verify init has called
    @Test
    fun initializeEngineOnStart() = runBlocking {
        Presenter(engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        coVerify { engine.init() }
    }

    @Test
    fun showKeyboardWhenEngineLoadCompleted() = runBlocking {
        coEvery { engine.init() } just Runs
        Presenter(engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        verify(timeout = 100) { view.showKeyboard() }
    }

    @Test
    fun whenTypeOneNumberThenDisplayResult() = runBlocking {
        withTimeout(3000) {
            val input = mockk<T9Engine.Input>()
            every { engine.startInput() } returns input
            val events = listOf(Event.KEY_PRESS.withData(Key.num4),
                    Event.KEY_PRESS.withData(Key.num2),
                    Event.KEY_PRESS.withData(Key.keySharp))
            every {
                input.push(any())
            } just Runs
            var inputted = 0
            every { input.confirmed } answers {
                inputted++
                inputted >= events.size
            }
            val cand = setOf("43")
            every { input.result() } returns cand
            Presenter(engine).attachView(view)
            events.forEach {
                view.eventSource.send(it)
            }
            verify(timeout = 100) { view.showCandidates(cand) }
        }
    }
}

