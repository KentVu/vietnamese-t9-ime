package com.vutrankien.t9vietnamese.tests

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

interface View {
    val scope: CoroutineScope
    val eventSource: Channel<Event>

    fun showProgress()
    fun showKeyboard()
}

enum class Event {
    START
}
