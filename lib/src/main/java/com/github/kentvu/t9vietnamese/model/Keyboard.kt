package com.github.kentvu.t9vietnamese.model

class Keyboard(private val key: Key) {
    fun findKey(c: Char): Key {
        return if (key.symbol == c) {
            key
        } else {
            throw IllegalArgumentException("No key found for $c!")
        }
    }

}
