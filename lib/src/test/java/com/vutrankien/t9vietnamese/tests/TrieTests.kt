package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.trie.Trie
import com.vutrankien.t9vietnamese.trie.TrieFactory
import io.kotlintest.TestCase
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TrieTests: StringSpec() {
    private lateinit var trie: Trie

    init {
        "contains" {
            trie.contains("a") shouldBe true
            trie.contains("b") shouldBe true
            trie.contains("c") shouldBe true
        }

        "find" {
            val shouldBe0: (Map.Entry<String, Int>) -> Unit = {
                it.value shouldBe 0
            }
            trie.search("a").entries.forAll(shouldBe0)
            trie.search("b").entries.forAll(shouldBe0)
            trie.search("c").entries.forAll(shouldBe0)
        }
    }

    override fun beforeTest(testCase: TestCase) {
        trie = TrieFactory.newTrie()
        trie.build("a\nb\nc".lineSequence())
    }
}