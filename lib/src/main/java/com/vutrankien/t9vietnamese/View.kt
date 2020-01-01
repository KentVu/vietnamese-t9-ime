package com.vutrankien.t9vietnamese

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

interface View {
    val scope: CoroutineScope
    val eventSource: Channel<EventWithData>

    fun showProgress()
    fun showKeyboard()
}

enum class Event {
    START,
    KEY_PRESS;

    fun withData(data: Char) = EventWithData(this, data)

    fun noData(): EventWithData = EventWithData(this, null)
}

data class EventWithData(val event: Event, val data: Char?)
