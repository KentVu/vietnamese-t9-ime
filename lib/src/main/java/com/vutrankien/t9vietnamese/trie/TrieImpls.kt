package com.vutrankien.t9vietnamese.trie

import org.trie4j.patricia.MapPatriciaTrie

object TrieFactory {
    fun newTrie(): Trie {
        return TakawitterTrie()
    }
}

private class TakawitterTrie : Trie {
    private val trie: MapPatriciaTrie<Int> = MapPatriciaTrie()

    override fun build(seed: Sequence<String>) {
        seed.forEach {
            trie.insert(it, 0)
        }
    }

    override fun search(prefix: String): Map<String, Int> {
        val trieResult = trie.commonPrefixSearch(prefix)
        return trieResult.map {
            it to trie.get(it)
        }.toMap()
    }

    override fun contains(key: String): Boolean {
        return trie.contains(key)
    }
}