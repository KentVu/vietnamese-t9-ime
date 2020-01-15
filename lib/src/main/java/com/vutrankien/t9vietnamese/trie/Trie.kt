package com.vutrankien.t9vietnamese.trie

interface Trie {
    fun put(key: String)
    fun find(prefix: String): Map<String, Int>
    fun contains(key: String): Boolean
}
