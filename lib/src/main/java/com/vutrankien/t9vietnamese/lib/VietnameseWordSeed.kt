/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.lib

import java.text.Normalizer

object VietnameseWordSeed {

    /* 774 0x306 COMBINING BREVE */
    private const val BREVE = '̆'
    /* 770 0x302 COMBINING CIRCUMFLEX ACCENT */
    private const val CIRCUMFLEX_ACCENT = '̂'
    /* 795 31B COMBINING HORN */
    private const val HORN = '̛'
    /* 803 323 COMBINING DOT BELOW */
    private const val DOT_BELOW = '̣'

    fun getFromJavaResource(): Lazy<Sequence<String>> {
        return lazy {
            val sortedWords = sortedSetOf<String>() // use String's "natural" ordering
            javaClass.classLoader.getResourceAsStream("/vi-DauMoi.dic").bufferedReader().useLines {
                it.forEach { line ->
                    sortedWords.add(line.decomposeVietnamese())
                }
            }
            sortedWords.asSequence()
        }
    }

    fun String.decomposeVietnamese(): String {
        return Normalizer.normalize(
            this,
            Normalizer.Form.NFKD
        )
            // rearrange intonation and vowel-mark order.
            .replace("([eE])$DOT_BELOW$CIRCUMFLEX_ACCENT".toRegex(), "$1$CIRCUMFLEX_ACCENT$DOT_BELOW")
            // recombine specific vowels.
            .replace(
                ("([aA][$BREVE$CIRCUMFLEX_ACCENT])|([uUoO]$HORN)|[oOeE]$CIRCUMFLEX_ACCENT").toRegex()
            ) {
                Normalizer.normalize(
                    it.value,
                    Normalizer.Form.NFKC
                )
            }

    }
}