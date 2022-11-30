package com.github.kentvu.t9vietnamese.model

interface Trie {
    fun load()
    fun prefixSearch(prefix: String): Set<String>
}