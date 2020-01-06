package com.vutrankien.t9vietnamese

import kotlinx.coroutines.Deferred

interface T9Engine {
    var initialized: Boolean

    fun init(): Deferred<Unit>
    fun startInput(): Input

    interface Input {
        fun input(key: Char)
        fun result(): List<String>
    }
}
