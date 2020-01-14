package com.vutrankien.t9vietnamese

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope

interface T9Engine {
    val pad: PadConfiguration
    var initialized: Boolean


    fun init(scope: CoroutineScope = GlobalScope): Deferred<Unit>
    fun startInput(): Input

    interface Input {
        val confirmed: Boolean

        fun push(key: Key)
        fun result(): List<String>
    }
}
