package com.github.kentvu.t9vietnamese.model

import java.io.InputStream
import java.text.Normalizer

class VietnameseWordList() : WordList by DecomposedVietnameseWords(
    VietnameseWordList::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!
)