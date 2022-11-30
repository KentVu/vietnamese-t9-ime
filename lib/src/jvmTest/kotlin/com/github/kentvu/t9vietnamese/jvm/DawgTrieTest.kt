package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.Trie
import kotlin.test.Test

internal class DawgTrieTest {
    @Test
    fun trieTest() {
        val trie: Trie =
            DawgTrie(DecomposedVietnameseWords(javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic")!!))
        trie.load()
        val search = trie.prefixSearch("chà")
        search.forEach { println(it) }
    }
}