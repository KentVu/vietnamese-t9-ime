package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.T9AppEvent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

interface T9App {
    val keyPad: KeyPad
    fun init()
    fun describe(): String

    fun type(c: Char): Flow<T9AppEvent> {
        return type(keyPad.findKey(c))
    }

    @OptIn(FlowPreview::class)
    fun type(s: String): Flow<T9AppEvent> {
        return flowOf(*s.toCharArray().toTypedArray()).flatMapConcat { type(keyPad.findKey(it)) }
        //return flow {
        //    s.forEach { c -> type(keyPad.findKey(c)) } }
    }

    fun type(key: Key): Flow<T9AppEvent>

}
