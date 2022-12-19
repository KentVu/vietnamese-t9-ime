package com.github.kentvu.t9vietnamese.model

class Keyboard(private val keys: List<Key>) {
    fun findKey(c: Char): Key {
        return keys.firstOrNull { key ->
            key.symbol == c
        } ?: throw IllegalArgumentException("No key found for $c!")
    }

}
