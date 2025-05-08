package com.github.kentvu.t9vietnamese.model

/** Predefined keypads configurations. */
object KeyPads {
    /** Vietnamese language. */
    val VN = object : KeyPad (
        Shift = Key(Action.Shift),
        keyBackspace = Key(Action.Backspace, longAction = Action.Clear),
        keyStar = Key(Action.Star),
        keyHash = Key(Action.Hash, longAction = Action.Return),
        keyOk = Key(Action.Ok),
        key1 = Key(Action.One, ".,?!"),
        key2 = Key(Action.Two, "aăâbć"),
        key3 = Key(Action.Three, "dđef̀ê"),
        key4 = Key(Action.Four, "ghỉ"),
        key5 = Key(Action.Five, "jkl̃"),
        key6 = Key(Action.Six, "mnọôơ"),
        key7 = Key(Action.Seven, "pqrs"),
        key8 = Key(Action.Eight, "tuưv"),
        key9 = Key(Action.Nine, "wxyz"),
        key0 = Key(Action.Space, longAction = Action.Zero),
    ) { override val punctualMarksKey: Key = key1 }
    /** Numeric layout. */
    val Numeric = object : KeyPad (
        Shift = null,
        keyBackspace = Key(Action.Backspace, longAction = Action.Clear),
        keyStar = Key(Action.Star, "./-+,?!"),
        keyHash = Key(Action.Hash, longAction = Action.Return),
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
        key0 = Key(Action.Zero, longAction = Action.Space),
    ) { override val punctualMarksKey: Key = keyStar }
}

/*
    val pad = KeyPad(listOf(
            key1, key2, key3,
            key4, key5, key6,
            key7, key8, key9,
            key0,
        ))
*/
