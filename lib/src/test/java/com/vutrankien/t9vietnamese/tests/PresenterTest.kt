package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.*
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
            val key = slot<Key>()
            val events = listOf(Event.KEY_PRESS.withData(Key.num4),
                    Event.KEY_PRESS.withData(Key.num2),
                    Event.KEY_PRESS.withData(Key.keySharp))
            var inputted = 0
            every {
                input.push(any()/*capture(key)*/)
            } just Runs
            every { input.confirmed } answers {
                inputted++
                inputted >= events.size
            }
            val cand = listOf("43")
            every { input.result() } returns cand
            Presenter(engine).attachView(view)
            for (eventWithData in events) {
                view.eventSource.send(eventWithData)
            }
            verify { view.showCandidates(cand) }
        }
    }
}

