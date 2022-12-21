package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Key

class KeySequence(keys: List<Key>) {

    private val keys = keys.toTypedArray()
    private val keyString = keys.map { it.symbol }.joinToString("")
    val length: Int = keys.size

    fun forEach(block: (Key) -> Unit) {
        keys.forEach(block)
    }

    override fun hashCode(): Int {
        return keyString.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
//        if (javaClass != other?.javaClass) return false

        other as KeySequence

        if (keyString != other.keyString) return false

        return true
    }

    fun asString(): String {
        return keyString
    }

    //private val keySeq: List<Key> = chars.map { StandardKeys.fromChar(it) }
}
