package com.vutrankien.t9vietnamese.tests

import io.kotlintest.TestCase
import io.kotlintest.assertSoftly
import io.kotlintest.inspectors.forAll
import io.kotlintest.matchers.maps.shouldContainKey
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kentvu.dawgjava.Trie
import kentvu.dawgjava.TrieFactory
import kentvu.dawgjava.WordSequence
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import java.time.Duration

//@UseExperimental(ObsoleteCoroutinesApi::class)
@ObsoleteCoroutinesApi
class TrieTests: StringSpec() {
    private lateinit var trie: Trie

    init {
        "build".config(timeout = Duration.ofMillis(200)) {
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
            withTimeout(100) {
                job.await()
            }
        }

        "contains" {
            // sorted
            trie.build(content_countries.lineSequence())
            trie.contains("Vietnam") shouldBe true
            trie.contains("Cambodia") shouldBe true
            trie.contains("Thailand") shouldBe true
            trie.contains("England") shouldBe false
        }

        "find" {
            trie.build(content.lineSequence())
            for(prefix in arrayOf("a", "b", "c")) {
                trie.search(prefix).let {
                    it.shouldContainKey(prefix)
                    it.entries.forAll(mapEntryValueShouldBe0)
                }
            }
        }

        "find2" {
            trie.build(content_countries.lineSequence())
            trie.search("V").let {
                it.shouldContainKey("Vietnam")
                it.entries.forAll(mapEntryValueShouldBe0)
            }
            trie.search("c").entries.forAll(mapEntryValueShouldBe0)
        }
    }

    override fun beforeTest(testCase: TestCase) = runBlocking {
        trie = TrieFactory.newTrie()
    }

    companion object {
        private const val content = "a\nb\nc"
        // sorted!
        val content_countries = """
                Cambodia
                Laos
                Thailand
                Venezuela
                Vietnam
                Việt
                countries
                """.trimIndent()
        val mapEntryValueShouldBe0: (Map.Entry<*, Int>) -> Unit = {
            it.value shouldBe 0
        }
    }
}

private fun String.wordSequence(): WordSequence {
    return WordSequence.new(this)
}
