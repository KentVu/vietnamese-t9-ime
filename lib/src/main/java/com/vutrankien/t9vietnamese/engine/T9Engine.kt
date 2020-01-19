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

    data class Event(val type: EventType, val data: Any?) {
        constructor(type: EventType) : this(type, null)
    }

    suspend fun init()
    fun startInput(): Input

    /**
     * Represent one input session.
     */
    interface Input {
        fun push(key: Key)
        val candidates: Set<String>
    }
}
