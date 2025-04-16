package com.github.kentvu.t9vietnamese.model

class KeyPad(private val keys: List<Key>) {
    fun findKey(c: Char): Key {
        return keys.firstOrNull { key ->
            key.action.rawChar == c
        } ?: throw IllegalArgumentException("No key found for $c!")
    }

    fun describe(): String {
        return keys.toString()
    }
}
