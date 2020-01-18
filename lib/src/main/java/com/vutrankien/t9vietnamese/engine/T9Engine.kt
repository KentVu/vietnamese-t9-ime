package com.vutrankien.t9vietnamese.engine

import com.vutrankien.t9vietnamese.Key
import com.vutrankien.t9vietnamese.PadConfiguration

interface T9Engine {
    val pad: PadConfiguration
    var initialized: Boolean


    suspend fun init()
    fun startInput(): Input

    interface Input {
        val confirmed: Boolean

        fun push(key: Key)
        fun result(): Set<String>
    }
}
