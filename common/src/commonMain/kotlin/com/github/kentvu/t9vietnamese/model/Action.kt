package com.github.kentvu.t9vietnamese.model

enum class Action(
    /** null if this is non-typing action, i.e. control action. */
    val rawChar: Char?,
    private val symbol: String = "", // can't use Char since 🆗 is not accepted by the JVM??
) {
    Clear(null, 'C'),
    Backspace(null, '⌫'),
    Star('*'),
    Hash('#'),
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
    Return('\n', '⏎'),;
    constructor(rawChar: Char?, displayText: Char)
            : this(rawChar, "$displayText")
    val displaySymbol: String
        get() = symbol.takeIf { it.isNotEmpty() } ?: "$rawChar"

    companion object {
        private val charMap =
            entries.associateBy { it.rawChar }
        fun fromChar(c: Char): Action {
            return charMap[c] ?: throw IllegalArgumentException("No Key for char '$c'.")
        }
    }
}
