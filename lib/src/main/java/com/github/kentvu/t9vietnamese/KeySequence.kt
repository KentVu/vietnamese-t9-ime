package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key
import com.github.kentvu.t9vietnamese.model.StandardKeys

class KeySequence(private val keys: List<Key>) {

    val length: Int = keys.size

    fun forEach(block: (Key) -> Unit) {
        keys.forEach(block)
    }

    //private val keySeq: List<Key> = chars.map { StandardKeys.fromChar(it) }
}
