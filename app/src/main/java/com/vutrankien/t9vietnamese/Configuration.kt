package com.vutrankien.t9vietnamese

val padConfigurations =
        mapOf<String, PadConfiguration>(
                LOCALE_VN to PadConfiguration(
                        num2 = linkedSetOf('a', 'ă', 'â', 'b', 'c'),
                        num3 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num4 = linkedSetOf('g', 'h', 'i', 'ê', 'f'),
                        num5 = linkedSetOf('j', 'k', 'l', 'ê', 'f'),
                        num6 = linkedSetOf('m', 'n', 'o', 'ê', 'f'),
                        num7 = linkedSetOf('p', 'q', 'r', 's', 'f'),
                        num8 = linkedSetOf('t', 'u', 'v', 'ê', 'f'),
                        num9 = linkedSetOf('w', 'x', 'y', 'z', 'f'),
                        num0 = linkedSetOf('d', 'đ', 'e', 'ê', 'f'),
                        num1 = linkedSetOf('d', 'đ', 'e', 'ê', 'f')
                )
        )

val wordListFiles = mapOf<String, String>(LOCALE_VN to "morphemes.txt")

class Configuration(locale: String) {
    val pad = padConfigurations[locale]
    val wordListFile = wordListFiles[locale]
    val dbname = mapOf<String, String>(
            LOCALE_VN to "t9vietnamese.db"
    )[locale]!!
}

data class PadConfiguration(
        val num1: Set<Char>,
        val num2: Set<Char>,
        val num3: Set<Char>,
        val num4: Set<Char>,
        val num5: Set<Char>,
        val num6: Set<Char>,
        val num7: Set<Char>,
        val num8: Set<Char>,
        val num9: Set<Char>,
        val num0: Set<Char>
)