package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import kotlinx.coroutines.channels.Channel

interface T9Engine {
    val initialized: Boolean
    /**
     * This NEED to be set before pushing anything to the engine!
     */
    var pad: PadConfiguration
    val eventSource: Channel<Event>

    enum class EventType {
        NEW_CANDIDATES,
        CONFIRM
    }

    sealed class Event {
        class NewCandidates(val candidates: Set<String>) : Event() {
            fun contains(other: NewCandidates): Boolean =
                candidates.containsAll(other.candidates)

            override fun toString(): String {
                return "NewCandidates:$candidates"
            }
        }
        object Confirm : Event()
        object Initialized : Event()
    }

    suspend fun init(seed: Sequence<String>)
    suspend fun push(key: Key)
}
