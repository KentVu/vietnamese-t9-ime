package com.vutrankien.t9vietnamese.trie

import org.trie4j.patricia.MapPatriciaTrie

class TakawitterTrie(seed: Sequence<String>) : Trie {
    private val trie: MapPatriciaTrie<Int> = MapPatriciaTrie()
    init {
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