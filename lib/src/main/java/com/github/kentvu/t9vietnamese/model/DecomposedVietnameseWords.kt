package com.github.kentvu.t9vietnamese.model

import java.io.InputStream

class DecomposedVietnameseWords(private val ins: InputStream) : WordList {
    override fun iterable(): Iterable<String> {
        return ins.bufferedReader().lineSequence().asIterable()
    }
}