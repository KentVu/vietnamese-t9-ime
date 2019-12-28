package com.vutrankien.t9vietnamese.tests

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PresenterTest {
    @Test
    fun showProgressIndicatorOnStart() = runBlocking {
        val view = mockk<View>()
        every { view.eventSource } returns Channel()
        Presenter().attachView(view)
        view.eventSource.send(Event.START)
    }
}
