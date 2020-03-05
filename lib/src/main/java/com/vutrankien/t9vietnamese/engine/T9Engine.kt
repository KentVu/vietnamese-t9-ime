package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.Event
import com.vutrankien.t9vietnamese.EventWithData
import com.vutrankien.t9vietnamese.Key
import com.vutrankien.t9vietnamese.PadConfiguration
import kotlinx.coroutines.channels.Channel

interface T9Engine {
    val initialized: Boolean
    var pad: PadConfiguration
    //fun setPadConfig(pad: PadConfiguration)
    val eventSource: Channel<Event>

    enum class EventType {
        NEW_CANDIDATES,
        CONFIRM
    }

    sealed class Event {
        class NewCandidates(val candidates: Set<String>) : Event()
        object Confirm : Event()
        object Initialized : Event()
    }

    suspend fun init(seed: Sequence<String>)
    fun push(key: Key)
}
