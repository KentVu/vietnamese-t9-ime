package com.vutrankien.t9vietnamese

import kotlinx.coroutines.channels.ReceiveChannel

interface T9Engine {
    val pad: PadConfiguration
    var initialized: Boolean


    suspend fun init()
    fun startInput(): Input

    interface Input {
        val confirmed: Boolean

        fun push(key: Key)
        fun result(): List<String>
    }
}
