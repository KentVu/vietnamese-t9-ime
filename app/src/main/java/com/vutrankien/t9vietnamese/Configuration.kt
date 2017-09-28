package com.vutrankien.t9vietnamese

import com.vutrankien.t9vietnamese.KeyTypes.*

val padConfigurations =
        mapOf<String, PadConfiguration>(
                LOCALE_VN to PadConfiguration(
                        num2 = KeyConfig(Normal, linkedSetOf('a', 'ă', 'â', 'b', 'c', '́'
                                /*sắc*/)),
                        num3 = KeyConfig(Normal, linkedSetOf('d', 'đ', 'e', 'ê', 'f', '̀'
                                /*huyền*/)),
                        num4 = KeyConfig(Normal, linkedSetOf('g', 'h', 'i', '̉' /*hỏi*/)),
                        num5 = KeyConfig(Normal, linkedSetOf('j', 'k', 'l', '̃' /*ngã*/)),
                        num6 = KeyConfig(Normal, linkedSetOf('m', 'n', 'o', 'ô', 'ơ', ' ' /*nặng*/)),
                        num7 = KeyConfig(Normal, linkedSetOf('p', 'q', 'r', 's')),
                        num8 = KeyConfig(Normal, linkedSetOf('t', 'u', 'ư', 'v')),
                        num9 = KeyConfig(Normal, linkedSetOf('w', 'x', 'y', 'z')),
                        num0 = KeyConfig(Space, linkedSetOf(' ')),
                        num1 = KeyConfig(Symbol, linkedSetOf('.', ',', '?', '!', '-')),
                        keyStar = KeyConfig(NextCandidate),
                        keySharp = KeyConfig(ToNum)
                )
        )

val wordListFiles = mapOf<String, String>(LOCALE_VN to "morphemes.txt")

class Configuration(locale: String) {
    private val UNSUPPORTED_MSG = "Locale $locale unsupported"

    val pad = padConfigurations[locale] ?:
            throw UnsupportedOperationException(UNSUPPORTED_MSG)
    val wordListFile = wordListFiles[locale]
    val dbname = mapOf<String, String>(
            LOCALE_VN to "t9vietnamese.db"
    )[locale] ?: throw UnsupportedOperationException(UNSUPPORTED_MSG)
}

enum class KeyTypes {
    Normal, Symbol, NextCandidate, Space, ToNum
}
data class KeyConfig(val type: KeyTypes, val chars: Set<Char> = emptySet())

data class PadConfiguration(
        val num1: KeyConfig,
        val num2: KeyConfig,
        val num3: KeyConfig,
        val num4: KeyConfig,
        val num5: KeyConfig,
        val num6: KeyConfig,
        val num7: KeyConfig,
        val num8: KeyConfig,
        val num9: KeyConfig,
        val num0: KeyConfig,
        val keyStar: KeyConfig,
        val keySharp: KeyConfig
) {
    private val num2Char = mapOf(
            '0' to num0,
            '1' to num1,
            '2' to num2,
            '3' to num3,
            '4' to num4,
            '5' to num5,
            '6' to num6,
            '7' to num7,
            '8' to num8,
            '9' to num9,
            '*' to keyStar,
            '#' to keySharp
    )

    operator fun get(c: Char) = num2Char[c] ?: throw UnsupportedOperationException("Unknown KEY '$c'")
}