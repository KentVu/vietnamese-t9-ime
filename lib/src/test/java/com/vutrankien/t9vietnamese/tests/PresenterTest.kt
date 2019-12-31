package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.T9Engine
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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

        engine = mockk()
    }

    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        withTimeout(1000) {
            Presenter(engine).attachView(view)
            view.eventSource.send(Event.START)
            verify { view.showProgress() }
        }
    }

    @Test
    fun initializeEngineOnStart() = runBlocking {
        every { engine.init() } returns Unit
        Presenter(engine).attachView(view)
        view.eventSource.send(Event.START)
        verify { engine.init() }
        confirmVerified(engine)
    }
}
