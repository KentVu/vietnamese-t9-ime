package com.github.kentvu.t9vietnamese.jvm

interface Trie {
    fun prefixSearch(prefix: String): Set<String>
}