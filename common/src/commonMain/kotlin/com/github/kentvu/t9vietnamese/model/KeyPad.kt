package com.github.kentvu.t9vietnamese.model

/** Collections of key slots. */
abstract class KeyPad(
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

    abstract val punctualMarksKey: Key
    private val keys = setOf(
        key1, key2, key3,
        key4, key5, key6,
        key7, key8, key9,
        key0,
    )

    fun findKey(a: Action): Key {
        return keys.firstOrNull { key ->
            key.action == a
        } ?: throw IllegalArgumentException("KeyPad: No key found for $a!")
    }

    fun findKey(c: Char): Key {
        return keys.firstOrNull { key ->
            key.action.rawChar == c
        } ?: throw IllegalArgumentException("KeyPad: No key found for $c!")
    }

    fun numericSubstitution(c: Char): String {
        return findKey(c).subChars ?: error("Not a number($c)!!")
    }

    fun numericSubstitution(a: Action): String {
        return findKey(a).subChars ?: error("Not a number($a)!!")
    }

    fun describe(): String {
        return keys.toString()
    }
}
