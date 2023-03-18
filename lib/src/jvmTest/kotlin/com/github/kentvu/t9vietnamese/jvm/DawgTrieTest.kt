package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.lib.DawgTrie
import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import okio.FileSystem
import kotlin.test.Test

internal class DawgTrieTest {
    @Test
    fun trieTest() {
        val trie: Trie =
            DawgTrie(
                VietnameseWordList,
                FileSystem.SYSTEM
            )
        trie.load()
        val search = trie.prefixSearch("chà")
        search.forEach { println(it) }
    }
}