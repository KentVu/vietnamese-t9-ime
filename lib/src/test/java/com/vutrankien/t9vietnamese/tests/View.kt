package com.vutrankien.t9vietnamese.tests

import kotlinx.coroutines.channels.Channel

interface View {
    val eventSource: Channel<Event>
}

enum class Event {
    START
}
