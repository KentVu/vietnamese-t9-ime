package com.vutrankien.t9vietnamese.lib

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel

interface View {
    val scope: CoroutineScope
    val eventSource: ReceiveChannel<EventWithData<Event, Key>>

    fun showProgress(bytes: Int)
    fun showKeyboard()
    fun showCandidates(candidates: Collection<String>)
    fun nextCandidate()
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
