package com.github.kentvu.t9vietnamese.model

/** Predefined keys for VNmese keypad. */
enum class VNKeys(
    override val action: Action,
    override val longAction: Action? = null
) : Key {
    Shift(Action.Shift),
    keyBackspace(Action.Backspace, Action.Clear),
    keyStar(Action.Star),
    keyHash(Action.Hash, Action.Return),
    keyOk(Action.Ok),
    key1(Action.One),
    key2(Action.Two),
    key3(Action.Three),
    key4(Action.Four),
    key5(Action.Five),
    key6(Action.Six),
    key7(Action.Seven),
    key8(Action.Eight),
    key9(Action.Nine),
    key0(Action.Space, Action.Zero),
    ;
    companion object {
        private val sym2Key =
            entries.associateBy { it.action.rawChar }
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
