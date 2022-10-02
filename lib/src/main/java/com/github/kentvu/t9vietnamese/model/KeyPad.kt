package com.github.kentvu.t9vietnamese.model

import kotlinx.coroutines.flow.Flow

abstract class KeyPad {
    abstract val keyEvents: Flow<Key>

    protected open fun type(vararg cs: Char) {
        cs.forEach { type(it) }
    }
    protected abstract suspend fun onType(c: Char)
}
