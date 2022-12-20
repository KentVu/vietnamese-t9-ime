package com.github.kentvu.t9vietnamese.model

object VietnameseWordList : WordList by DecomposedVietnameseWords(
    VietnameseWordList::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!
) {
    override fun toString(): String {
        return "VietnameseWordList"
    }
}