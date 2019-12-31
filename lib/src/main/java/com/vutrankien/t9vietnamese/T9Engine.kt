package com.vutrankien.t9vietnamese

import kotlinx.coroutines.Deferred

interface T9Engine {
    val initialized: Boolean

    fun init(): Deferred<Unit>
}
