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
        Key.Num0 to KeyConfig(
            Confirm,
            linkedSetOf(' ')
        ),
        Key.Num1 to KeyConfig(
            Symbol,
            linkedSetOf('.', ',', '?', '!', '-')
        ),
        Key.Num2 to KeyConfig(
            Normal,
            linkedSetOf('a', 'ă', 'â', 'b', 'c', '́' /*sắc*/)
        ),
        Key.Num3 to KeyConfig(
            Normal,
            linkedSetOf('d', 'đ', 'e', 'ê', 'f', '̀' /*huyền*/)
        ),
        Key.Num4 to KeyConfig(
            Normal,
            linkedSetOf('g', 'h', 'i', '̉' /*hỏi*/)
        ),
        Key.Num5 to KeyConfig(
            Normal,
            linkedSetOf('j', 'k', 'l', '̃' /*ngã*/)
        ),
        Key.Num6 to KeyConfig(
            Normal,
            linkedSetOf('m', 'n', 'o', 'ô', 'ơ', '̣' /*nặng*/)
        ),
        Key.Num7 to KeyConfig(
            Normal,
            linkedSetOf('p', 'q', 'r', 's')
        ),
        Key.Num8 to KeyConfig(
            Normal,
            linkedSetOf('t', 'u', 'ư', 'v')
        ),
        Key.Num9 to KeyConfig(
            Normal,
            linkedSetOf('w', 'x', 'y', 'z')
        ),
        Key.Star to KeyConfig(
            NextCandidate
        ),
        Key.KeySharp to KeyConfig(
            ToNum
        ),
        Key.Left to KeyConfig(
            PrevCandidate
        ),
        Key.Backspace to KeyConfig(
            Backspace
        )
    )
)

enum class KeyType(val isConfirmationKey: Boolean) {
    Normal(false), Symbol(false), NextCandidate(false),
    Confirm(true), ToNum(false), PrevCandidate(false), Backspace(false);
}

data class KeyConfig(val type: KeyType, val chars: Set<Char> = emptySet()) {
}

enum class Key(
    /**
     * The character that represents this [Key], usually a digit.
     */
    val keycode: Int
) {

    Num0('0'.toInt()),
    Num1('1'.toInt()),
    Num2('2'.toInt()),
    Num3('3'.toInt()),
    Num4('4'.toInt()),
    Num5('5'.toInt()),
    Num6('6'.toInt()),
    Num7('7'.toInt()),
    Num8('8'.toInt()),
    Num9('9'.toInt()),
    Star('*'.toInt()),
    KeySharp('#'.toInt()),
    Left('<'.toInt()),
    Backspace(67);
    val char: Char
        get() = keycode.toChar()

    companion object {
        fun fromCode(code: Int): Key {
            return values().first { it.keycode == code }
            //values().forEach {
            //    if (it.char == num) {
            //        return it
            //    }
            //}
            //throw IllegalArgumentException("Key(char=${key.char})")
        }

        fun fromChar(c: Char): Key {
            return values().first { it.keycode == c.toInt() }
        }
    }
}

class PadConfiguration(private val configMap: Map<Key, KeyConfig>) {
    operator fun get(key: Key): KeyConfig {
        return configMap[key] ?: throw IllegalArgumentException("No config for key:$key")
    }

}
