/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
    }

    suspend fun init(seed: Sequence<String>)
    /**
     * Init from built db.
     */
    fun initFromDb()
    suspend fun push(key: Key)
    fun canReuseDb(): Boolean
}
