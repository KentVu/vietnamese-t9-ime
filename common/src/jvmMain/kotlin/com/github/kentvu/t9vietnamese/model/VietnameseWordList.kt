package com.github.kentvu.t9vietnamese.model

import okio.source

actual object VietnameseWordList : WordList by DecomposedVietnameseWords(
    DecomposedVietnameseWords::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!.source()
) {
    override fun toString(): String {
        return "JvmVietnameseWordList"
    }
}