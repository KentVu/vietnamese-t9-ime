package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.Event
import com.vutrankien.t9vietnamese.Presenter
import com.vutrankien.t9vietnamese.T9Engine
import com.vutrankien.t9vietnamese.View
import io.mockk.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Before
import org.junit.Test

class PresenterTest {
    lateinit var view: View
    lateinit var engine: T9Engine

    @Before
    fun setUp() {
        view = mockk(relaxUnitFun = true)
        every { view.eventSource } returns Channel()
        every { view.scope } returns GlobalScope

        engine = mockk(relaxUnitFun = true)
        every { engine.init() } returns GlobalScope.async { }
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
        verify { engine.init() }
        confirmVerified(engine)
    }

    @Test
    fun showKeyboardWhenEngineLoadCompleted() = runBlocking {
        val deferred = async { }
        every { engine.init() } returns deferred
        Presenter(engine).attachView(view)
        view.eventSource.send(Event.START.noData())
        deferred.await()
        verify { view.showKeyboard() }
    }

    @Test
    fun whenTypeOneNumberThenDisplayResult() = runBlocking {
        withTimeout(3000) {
            val input = mockk<T9Engine.Input>()
            every { engine.startInput() } returns input
            every { input.input(any()) } just Runs
            val cand = listOf("43")
            every { input.result() } returns cand
            Presenter(engine).attachView(view)
            view.eventSource.send(Event.KEY_PRESS.withData('4'))
            view.eventSource.send(Event.KEY_PRESS.withData('2'))
            view.eventSource.send(Event.KEY_PRESS.withData(' '))
            verify { view.showCandidates(cand) }
        }
    }
}

