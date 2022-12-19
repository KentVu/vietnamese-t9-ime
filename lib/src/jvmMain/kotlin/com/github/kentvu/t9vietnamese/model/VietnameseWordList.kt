package com.github.kentvu.t9vietnamese.model

import java.io.InputStream
import java.text.Normalizer

object VietnameseWordList : WordList by DecomposedVietnameseWords(
    VietnameseWordList::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!
) {
    override fun toString(): String {
        return "VietnameseWordList"
    }
}