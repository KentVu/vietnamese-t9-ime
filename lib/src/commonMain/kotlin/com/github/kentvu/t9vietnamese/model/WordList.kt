package com.github.kentvu.t9vietnamese.model

interface WordList {
    fun iterable(): Iterable<String>
    fun toSet(): Set<String>

    class Default(private val list: Set<String>): WordList {
        override fun iterable(): Iterable<String> {
            return list
        }

        override fun toSet(): Set<String> {
            return list
        }
    }
}
