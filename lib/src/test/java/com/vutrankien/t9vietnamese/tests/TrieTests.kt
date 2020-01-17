package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.trie.TakawitterTrie
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TrieTests: StringSpec({
    "contains" {
        val trie = TakawitterTrie("a\nb\nc".lineSequence())
        trie.contains("a") shouldBe true
        trie.contains("b") shouldBe true
        trie.contains("c") shouldBe true
    }
})