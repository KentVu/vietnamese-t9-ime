package com.github.kentvu.t9vietnamese.model

object VNKeys {
    fun fromChar(c: Char): Key {
        return sym2Key[c] ?: throw IllegalArgumentException("No Key for char '$c'.")
    }

    private val sym2Key = mutableMapOf<Char, Key>()
    val Clear: Key = Key('C', "").apply { sym2Key[symbol] = this }
    val key1: Key = Key('1', ".,?").apply { sym2Key[symbol] = this }
    val key2: Key = Key('2', "abć").apply { sym2Key[symbol] = this }
    val key3: Key = Key('3', "def̀").apply { sym2Key[symbol] = this }
    val key4: Key = Key('4', "ghỉ").apply { sym2Key[symbol] = this }
    val key5: Key = Key('5', "jkl̃").apply { sym2Key[symbol] = this }
    val key6: Key = Key('6', "mnọ").apply { sym2Key[symbol] = this }
    val key7: Key = Key('7', "pqrs").apply { sym2Key[symbol] = this }
    val key8: Key = Key('8', "tuv").apply { sym2Key[symbol] = this }
    val key9: Key = Key('9', "wxyz").apply { sym2Key[symbol] = this }
    val key0: Key = Key('0', " ").apply { sym2Key[symbol] = this }
    val keyStar: Key = Key('*', "").apply { sym2Key[symbol] = this }

    val all = listOf(
        key1, key2, key3,
        key4, key5, key6,
        key7, key8, key9,
        key0,
    )

}
