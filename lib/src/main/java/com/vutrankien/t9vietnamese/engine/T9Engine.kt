package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import com.vutrankien.t9vietnamese.lib.Seed
import kotlinx.coroutines.channels.ReceiveChannel

interface T9Engine {

    sealed class Event {
        class NewCandidates(val candidates: Collection<String>) : Event() {
            fun contains(other: NewCandidates): Boolean =
                candidates.containsAll(other.candidates)

            override fun toString(): String {
                return "T9Event.NewCandidates:$candidates"
            }
        }
        class Confirm(val word: String) : Event() {
            override fun equals(other: Any?): Boolean =
                if (other is Confirm)
                    other.word == word
                else super.equals(other)

            override fun hashCode(): Int = word.hashCode()
            override fun toString(): String = "T9Event.Confirm:$word"
        }
        class LoadProgress(val bytes: Int) : Event()
        object Initialized : Event()
        data class SelectCandidate(val selectedCandidate: Int) : Event()
        object Backspace : Event()
    }

    val engineSeed: Seed
    val initialized: Boolean
    /**
     * This NEED to be set before pushing anything to the engine!
     */
    val pad: PadConfiguration
    val eventSource: ReceiveChannel<Event>

    suspend fun push(key: Key)
    suspend fun init()
}
