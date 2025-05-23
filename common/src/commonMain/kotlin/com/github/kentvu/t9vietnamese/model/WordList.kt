package com.github.kentvu.t9vietnamese.model

interface WordList {
    val name: String

    fun toSet(): Set<String>

    class SetWordList(private val list: Set<String>, override val name: String): WordList {
        override fun toSet(): Set<String> {
            return list
        }
    }
}
