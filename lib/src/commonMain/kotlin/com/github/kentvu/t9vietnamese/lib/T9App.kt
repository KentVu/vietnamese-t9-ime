package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.T9AppEvent
import kotlinx.coroutines.flow.SharedFlow

interface T9App {
    val eventFlow: SharedFlow<T9AppEvent>
    val keyPad: KeyPad
    fun init()
    fun describe(): String
    fun type(c: Char) {
        type(keyPad.findKey(c))
    }
    fun type(s: String) {
        s.forEach { c -> type(keyPad.findKey(c)) }
    }

    fun type(key: Key)

}
