package com.github.kentvu.t9vietnamese.model

/** Collections of key slots. */
class KeyPad(
    val Shift: Key,
    val keyBackspace: Key,
    val keyStar: Key,
    val keyHash: Key,
    val keyOk: Key,
    /*abstract */val key1: Key,
    val key2: Key,
    val key3: Key,
    val key4: Key,
    val key5: Key,
    val key6: Key,
    val key7: Key,
    val key8: Key,
    val key9: Key,
    val key0: Key,
) {
    
    private val keys = setOf(
        key1, key2, key3,
        key4, key5, key6,
        key7, key8, key9,
        key0,
    )

    fun findKey(c: Char): Key {
        return keys.firstOrNull { key ->
            key.action.rawChar == c
        } ?: throw IllegalArgumentException("No key found for $c!")
    }

    fun describe(): String {
        return keys.toString()
    }
}
