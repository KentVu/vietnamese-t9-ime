package com.github.kentvu.t9vietnamese.model

import com.github.kentvu.t9vietnamese.model.Key
import kotlinx.coroutines.channels.ReceiveChannel

interface KeyPad {
    val keyEvents: KeyPadOutput

}

typealias KeyPadOutput = ReceiveChannel<Key>
