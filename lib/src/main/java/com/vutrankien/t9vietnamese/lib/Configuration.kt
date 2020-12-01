package com.vutrankien.t9vietnamese.lib

import com.vutrankien.t9vietnamese.lib.KeyType.*

const val LOCALE_VN = "vi-VN"
const val LOCALE_US = "en-US"
val DummyKeyConfig =
    KeyConfig(Normal, linkedSetOf())

val wordListFiles = mapOf<String, String>(LOCALE_VN to "morphemes.txt")

fun UNSUPPORTED_MSG(locale: String) = "Locale $locale unsupported"

//val configurations = mapOf(LOCALE_VN to VNConfiguration)

interface Configuration {
    val pad: PadConfiguration
    val wordListFile: String
    val dbname: String
}

object VNConfiguration: Configuration {
    private val locale = LOCALE_VN

    override val pad = //padConfigurations[locale] ?:
            throw UnsupportedOperationException(
                UNSUPPORTED_MSG(
                    locale
                )
            )
    override val wordListFile = wordListFiles[locale] ?: throw UnsupportedOperationException(
        UNSUPPORTED_MSG(locale)
    )
    override val dbname = mapOf<String, String>(
            LOCALE_VN to "t9vietnamese.db"
    )[locale] ?: throw UnsupportedOperationException(
        UNSUPPORTED_MSG(locale)
    )
}

// TODO match case-insensitively
val VnPad = PadConfiguration(
    mapOf(
        Key.num0 to KeyConfig(
            Confirm,
            linkedSetOf(' ')
        ),
        Key.num1 to KeyConfig(
            Symbol,
            linkedSetOf('.', ',', '?', '!', '-')
        ),
        Key.num2 to KeyConfig(
            Normal,
            linkedSetOf('a', 'ă', 'â', 'b', 'c', '́' /*sắc*/)
        ),
        Key.num3 to KeyConfig(
            Normal,
            linkedSetOf('d', 'đ', 'e', 'ê', 'f', '̀' /*huyền*/)
        ),
        Key.num4 to KeyConfig(
            Normal,
            linkedSetOf('g', 'h', 'i', '̉' /*hỏi*/)
        ),
        Key.num5 to KeyConfig(
            Normal,
            linkedSetOf('j', 'k', 'l', '̃' /*ngã*/)
        ),
        Key.num6 to KeyConfig(
            Normal,
            linkedSetOf('m', 'n', 'o', 'ô', 'ơ', '̣' /*nặng*/)
        ),
        Key.num7 to KeyConfig(
            Normal,
            linkedSetOf('p', 'q', 'r', 's')
        ),
        Key.num8 to KeyConfig(
            Normal,
            linkedSetOf('t', 'u', 'ư', 'v')
        ),
        Key.num9 to KeyConfig(
            Normal,
            linkedSetOf('w', 'x', 'y', 'z')
        ),
        Key.star to KeyConfig(
            NextCandidate
        ),
        Key.keySharp to KeyConfig(
            ToNum
        )
    )
)

enum class KeyType(val isConfirmationKey: Boolean) {
    Normal(false), Symbol(false), NextCandidate(false),
    Confirm(true), ToNum(false);
}

data class KeyConfig(val type: KeyType, val chars: Set<Char> = emptySet()) {
}

enum class Key(
    /**
     * The character that represents this [Key], usually a digit.
     */
    val char: Char
) {
    num0('0'),
    num1('1'),
    num2('2'),
    num3('3'),
    num4('4'),
    num5('5'),
    num6('6'),
    num7('7'),
    num8('8'),
    num9('9'),
    star('*'),
    keySharp('#');

    companion object {
        fun fromNum(num: Char): Key {
            return values().first { it.char == num }
            //values().forEach {
            //    if (it.char == num) {
            //        return it
            //    }
            //}
            //throw IllegalArgumentException("Key(char=${key.char})")
        }
    }
}

class PadConfiguration(private val configMap: Map<Key, KeyConfig>) {
    operator fun get(key: Key): KeyConfig {
        return configMap[key] ?: throw IllegalArgumentException("No config for key:$key")
    }

}
