package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import kotlinx.coroutines.channels.ReceiveChannel

interface KeyPad {
    val keyEvents: KeyEvent

    interface KeyEvent {
        fun receive(key: Key)
    }
}
