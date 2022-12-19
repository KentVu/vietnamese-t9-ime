package com.github.kentvu.t9vietnamese.model

import java.io.InputStream
import java.text.Normalizer

class DecomposedVietnameseWords(private val ins: InputStream) : WordList {

    private val sortedWords by lazy {
        // use String's "natural" ordering
        sortedSetOf<String>().apply {
            println("Building sortedWords...")
            ins.bufferedReader().useLines {
                it.forEach { line ->
                    add(line.decomposeVietnamese())
                }
            }
        }
    }

    override fun iterable(): Iterable<String> {
        return sortedWords
    }

    override fun toSet(): Set<String> {
        return sortedWords
    }

    private fun String.decomposeVietnamese(): String {
        return Normalizer.normalize(
            this,
            Normalizer.Form.NFKD
        )
            // rearrange intonation and vowel-mark order.
            .replace("([eE])${DOT_BELOW}${CIRCUMFLEX_ACCENT}".toRegex(), "$1${CIRCUMFLEX_ACCENT}${DOT_BELOW}")
            // recombine specific vowels.
            .replace(
                ("([aA][${BREVE}${CIRCUMFLEX_ACCENT}])|([uUoO]${HORN})|[oOeE]${CIRCUMFLEX_ACCENT}").toRegex()
            ) {
                Normalizer.normalize(
                    it.value,
                    Normalizer.Form.NFKC
                )
            }
    }

    companion object {
        /* 774 0x306 COMBINING BREVE */
        private const val BREVE = '̆'

        /* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
        private const val CIRCUMFLEX_ACCENT = '̂'

        /* 795 31B COMBINING HORN */
        private const val HORN = '̛'

        /* 803 323 COMBINING DOT BELOW */
        private const val DOT_BELOW = '̣'
    }
}