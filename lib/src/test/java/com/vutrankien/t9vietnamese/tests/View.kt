package com.vutrankien.t9vietnamese.tests

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

interface View {
    val scope: CoroutineScope
    val eventSource: Channel<Event>

    fun showProgress()
}

enum class Event {
    START
}
