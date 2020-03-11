package com.vutrankien.t9vietnamese.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel

interface View {
    val scope: CoroutineScope
    val eventSource: Channel<EventWithData<Event, Key>>

    fun showProgress()
    fun showKeyboard()
    fun showCandidates(cand: Set<String>)
    fun confirmInput(word: String)
}

enum class Event {
    START,
    KEY_PRESS;

    fun withData(data: Key) =
        EventWithData(this, data)
    fun noData(): EventWithData<Event, Key> =
        EventWithData(this, null)
}

data class EventWithData<TEvent, TData>(val event: TEvent, val data: TData?)
