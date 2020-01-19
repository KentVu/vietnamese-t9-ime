package com.vutrankien.t9vietnamese.tests

import com.vutrankien.t9vietnamese.trie.Trie
import com.vutrankien.t9vietnamese.trie.TrieFactory
import io.kotlintest.TestCase
import io.kotlintest.assertSoftly
import io.kotlintest.inspectors.forAll
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.runBlocking

//@UseExperimental(ObsoleteCoroutinesApi::class)
@ObsoleteCoroutinesApi
class TrieTests: StringSpec() {
    private lateinit var trie: Trie

    init {
        "build" {
            val channel = Channel<Int>()
            val job = GlobalScope.async {
                val progress = channel.toList()
                assertSoftly {
                    progress[0] shouldBe 2
                    progress[1] shouldBe 4
                    progress[2] shouldBe 6
                }
            }
            trie.build(content.lineSequence(), channel)
            job.await()
        }

        "contains" {
            trie.build(content.lineSequence())
            trie.contains("a") shouldBe true
            trie.contains("b") shouldBe true
            trie.contains("c") shouldBe true
        }

        "find" {
            trie.build(content.lineSequence())
            val shouldBe0: (Map.Entry<String, Int>) -> Unit = {
                it.value shouldBe 0
            }
            trie.search("a").entries.forAll(shouldBe0)
            trie.search("b").entries.forAll(shouldBe0)
            trie.search("c").entries.forAll(shouldBe0)
        }
    }

    override fun beforeTest(testCase: TestCase) = runBlocking {
        trie = TrieFactory.newTrie()
    }

    companion object {
        private const val content = "a\nb\nc"
    }
}
