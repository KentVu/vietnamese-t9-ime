package com.github.kentvu.t9vietnamese.model

/** Predefined keys for VNmese keypad. */
val VNKeys = KeyPad (
    Shift = Key(Action.Shift),
    keyBackspace = Key(Action.Backspace, Action.Clear),
    keyStar = Key(Action.Star),
    keyHash = Key(Action.Hash, Action.Return),
    keyOk = Key(Action.Ok),
    key1 = Key(Action.One),
    key2 = Key(Action.Two),
    key3 = Key(Action.Three),
    key4 = Key(Action.Four),
    key5 = Key(Action.Five),
    key6 = Key(Action.Six),
    key7 = Key(Action.Seven),
    key8 = Key(Action.Eight),
    key9 = Key(Action.Nine),
    key0 = Key(Action.Space, Action.Zero),
)/*

    val pad = KeyPad(listOf(
            key1, key2, key3,
            key4, key5, key6,
            key7, key8, key9,
            key0,
        ))
*/
