package com.vutrankien.t9vietnamese.tests

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.junit.Test

class PresenterTest {
    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        withTimeout(1000) {
            val view = mockk<View>(relaxUnitFun = true)
            every { view.eventSource } returns Channel()
            every { view.scope } returns GlobalScope
            Presenter().attachView(view)
            view.eventSource.send(Event.START)
            verify { view.showProgress() }
        }
    }
}
