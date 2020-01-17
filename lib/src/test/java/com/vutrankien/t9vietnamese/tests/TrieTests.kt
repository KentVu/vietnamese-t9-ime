package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.trie.TakawitterTrie
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TrieTests: StringSpec({
    "contains" {
        val trie = TakawitterTrie("a\nb\nc".lineSequence())
        trie.contains("a") shouldBe true
        trie.contains("b") shouldBe true
        trie.contains("c") shouldBe true
    }

    "find" {
        val trie = TakawitterTrie("a\nb\nc".lineSequence())
        val shouldBe0: (Map.Entry<String, Int>) -> Unit = {
            it.value shouldBe 0
        }
        trie.search("a").entries.forAll(shouldBe0)
        trie.search("b").entries.forAll(shouldBe0)
        trie.search("c").entries.forAll(shouldBe0)
    }
})