package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.Event
import com.vutrankien.t9vietnamese.EventWithData
import com.vutrankien.t9vietnamese.Key
import com.vutrankien.t9vietnamese.PadConfiguration
import kotlinx.coroutines.channels.Channel

interface T9Engine {
    var initialized: Boolean
    val pad: PadConfiguration
    val eventSource: Channel<Event>

    enum class EventType {
        NEW_CANDIDATES,
        CONFIRM
    }

    sealed class Event {
        class NewCandidates(val candidates: Set<String>) : Event()
        object Confirm : Event()
    }

    suspend fun init(seed: Sequence<String>)
    fun push(key: Key)
    val candidates: Set<String>
}
