package com.github.kentvu.t9vietnamese.model

import doist.x.normalize.Form
import doist.x.normalize.normalize
import okio.Sink
import okio.Source
import okio.buffer
import okio.use

class DecomposedVietnameseWords(private val ins: Source) : WordList {

    private val sortedWords by lazy {
        // use String's "natural" ordering
        mutableSetOf<String>().apply {
            println("Building sortedWords...")
            ins.buffer().use {
                add(it.readUtf8Line()?.decomposeVietnamese() ?: return@use)
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
        return normalize(Form.NFKD)
            // rearrange intonation and vowel-mark order.
            .replace("([eE])$DOT_BELOW$CIRCUMFLEX_ACCENT".toRegex(), "$1$CIRCUMFLEX_ACCENT$DOT_BELOW")
            // recombine specific vowels.
            .replace(
                "([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oOeE]$CIRCUMFLEX_ACCENT".toRegex()
            ) { it.value.normalize(Form.NFKC) }
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