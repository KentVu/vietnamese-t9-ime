package com.github.kentvu.t9vietnamese.model

import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.View

interface T9App {
    val candidates: Set<String>
    val keyPad: KeyPad
    fun init()
    fun describe(): String
    fun type(c: Char) {
        type(keyPad.findKey(c))
    }

    fun type(key: Key)

}
