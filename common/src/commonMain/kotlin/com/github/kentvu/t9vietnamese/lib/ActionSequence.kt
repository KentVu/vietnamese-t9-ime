package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Action

class ActionSequence(actions: List<Action>) {

    private val keys = actions.toTypedArray()
    private val keyString = actions.map { it.rawChar }.joinToString("")
    val length: Int = actions.size

    fun forEach(block: (Action) -> Unit) {
        keys.forEach(block)
    }

    override fun hashCode(): Int {
        return keyString.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
//        if (javaClass != other?.javaClass) return false

        other as ActionSequence

        if (keyString != other.keyString) return false

        return true
    }

    fun asString(): String {
        return keyString
    }

    //private val keySeq: List<Key> = chars.map { StandardKeys.fromChar(it) }
}