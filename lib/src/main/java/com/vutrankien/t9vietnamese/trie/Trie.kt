package com.vutrankien.t9vietnamese.trie

interface Trie {
    fun search(prefix: String): Map<String, Int>
    fun contains(key: String): Boolean
}
