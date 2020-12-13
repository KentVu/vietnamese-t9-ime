package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.lib.Key
import com.vutrankien.t9vietnamese.lib.PadConfiguration
import kotlinx.coroutines.channels.Channel

interface T9Engine {
    val initialized: Boolean
    /**
     * This NEED to be set before pushing anything to the engine!
     */
    val pad: PadConfiguration
    val eventSource: Channel<Event>

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
    }

    suspend fun init(seed: Sequence<String>)
    /**
     * Init from built db.
     */
    fun initFromDb()
    suspend fun push(key: Key)
    fun canReuseDb(): Boolean
}
