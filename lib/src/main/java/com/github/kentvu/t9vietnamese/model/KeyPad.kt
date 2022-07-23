package com.github.kentvu.t9vietnamese.model

import kotlinx.coroutines.flow.Flow

abstract class KeyPad {
    abstract val keyEvents: Flow<Key>

    protected open suspend fun type(vararg cs: Char) {
        cs.forEach { type(it) }
    }
    protected abstract suspend fun type(c: Char)
}
