package com.github.kentvu.t9vietnamese.model

enum class Action(
    /** null if this is non-typing action, i.e. control action. */
    val rawChar: Char?,
    val displayText: String = "", // can't use Char since 🆗 is not accepted by the JVM??
) {
    Clear(null, 'C'),
    Backspace(null, '⌫'),
    Star(null, '*'),
    Hash(null, '#'),
    Shift(null, '⇧'),
    Ok(null, "🆗"), // ✔,↵,
    One('1'),
    Two('2'),
    Three('3'),
    Four('4'),
    Five('5'),
    Six('6'),
    Seven('7'),
    Eight('8'),
    Nine('9'),
    Zero('0'),
    Space(' ', '␣'),
    Return(null, '⏎'),;
    constructor(rawChar: Char?, displayText: Char)
            : this(rawChar, "$displayText")
    val symbol: String
        get() = displayText.takeIf { it.isNotEmpty() } ?: "$rawChar"
}
