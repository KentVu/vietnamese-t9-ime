package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.StandardKeys
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import org.jetbrains.annotations.TestOnly

class DummyKeyPad(): KeyPad() {
    @TestOnly
    suspend fun simulateTyping(vararg cs: Char) {
        type(*cs)
    }

    private val _keyEvent = MutableSharedFlow<Key>()
    override val keyEvents: Flow<Key> = _keyEvent
    override suspend fun type(c: Char) {
        _keyEvent.emit(StandardKeys.fromChar(c))
    }
}
