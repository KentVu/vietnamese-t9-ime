package com.github.kentvu.t9vietnamese.model

/** Predefined keys for VNmese keypad. */
enum class VNKeys(
    override val action: Action,
    override val subChars: String,
    override val longAction: Action? = null
) : Key {
    Shift(Action.Shift, ""),//.apply { sym2Key[action.symbol] = this }
    keyBackspace(Action.Backspace, "C", Action.Clear),//.apply { sym2Key[action.symbol] = this }
    keyStar(Action.Star, ""),//.apply { sym2Key[action.symbol] = this }
    keyHash(Action.Hash, "⏎", Action.Return),//.apply { sym2Key[action.symbol] = this }
    keyOk(Action.Ok, ""),//.apply { sym2Key[action.symbol] = this }
    key1(Action.One, ".,?"),//.apply { sym2Key[action.symbol] = this }
    key2(Action.Two, "aăâbć"),//.apply { sym2Key[action.symbol] = this }
    key3(Action.Three, "dđef̀ê"),//.apply { sym2Key[action.symbol] = this }
    key4(Action.Four, "ghỉ"),//.apply { sym2Key[action.symbol] = this }
    key5(Action.Five, "jkl̃"),//.apply { sym2Key[action.symbol] = this }
    key6(Action.Six, "mnọôơ"),//.apply { sym2Key[action.symbol] = this }
    key7(Action.Seven, "pqrs"),//.apply { sym2Key[action.symbol] = this }
    key8(Action.Eight, "tuưv"),//.apply { sym2Key[action.symbol] = this }
    key9(Action.Nine, "wxyz"),//.apply { sym2Key[action.symbol] = this }
    key0(Action.Space, "0", Action.Zero),//.apply { sym2Key[action.symbol] = this }
    ;
    companion object {
        private val sym2Key =
            entries.associateBy { it.action.rawChar /*symbol.first()*/ }
        fun fromChar(c: Char): Key {
            return sym2Key[c] ?: throw IllegalArgumentException("No Key for char '$c'.")
        }
    }

    /*
    val pad = KeyPad(listOf(
            key1, key2, key3,
            key4, key5, key6,
            key7, key8, key9,
            key0,
        ))
     */
}
