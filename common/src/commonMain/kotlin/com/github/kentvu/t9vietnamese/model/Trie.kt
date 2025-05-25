package com.github.kentvu.t9vietnamese.model

interface Trie {
    suspend fun load()
    fun prefixSearch(prefix: String): Set<String>
    fun containsPrefix(prefix: String): Boolean
}