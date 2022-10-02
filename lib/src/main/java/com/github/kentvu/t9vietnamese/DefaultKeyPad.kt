package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.StandardKeys
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.jetbrains.annotations.TestOnly

class DummyKeyPad(): KeyPad() {
    @TestOnly
    suspend fun simulateTyping(cs: String) {
        for (c in cs) {
            onType(c)
        }
    }

    private val _keyEvent = MutableSharedFlow<Key>()
    override val keyEvents: SharedFlow<Key> = _keyEvent.asSharedFlow()
    override suspend fun onType(c: Char) {
        _keyEvent.emit(StandardKeys.fromChar(c))
    }
}
