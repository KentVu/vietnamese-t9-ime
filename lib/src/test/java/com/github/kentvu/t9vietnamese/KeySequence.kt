package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.StandardKeys

class KeySequence(vararg chars: Char) {
    fun forEach(block: (Key) -> Unit) {
        keySeq.forEach(block)
    }

    private val keySeq: List<Key> = chars.map { StandardKeys.fromChar(it) }
}
