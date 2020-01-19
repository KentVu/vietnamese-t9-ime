package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.Event
import com.vutrankien.t9vietnamese.EventWithData
import com.vutrankien.t9vietnamese.Key
import com.vutrankien.t9vietnamese.PadConfiguration
import kotlinx.coroutines.channels.Channel

interface T9Engine {
    val pad: PadConfiguration
    val eventSource: Channel<Event>

    enum class Event {
        NEW_CANDIDATES,
        CONFIRM;

        /**
         * @param data Set of candidates
         */
        fun withData(data: Set<String>) = EventWithData(this, data)
        fun noData(): EventWithData<Event, Set<String>> = EventWithData(this, null)
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
