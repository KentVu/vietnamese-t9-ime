package com.vutrankien.t9vietnamese.trie

import com.vutrankien.t9vietnamese.ESTIMATED_LINEBREAK_SIZE
import kotlinx.coroutines.channels.Channel
import org.trie4j.patricia.MapPatriciaTrie

private val String.size: Int
    get() = toByteArray().size

object TrieFactory {
    fun newTrie(): Trie {
        return TakawitterTrie()
    }
}

private class TakawitterTrie : Trie {
    private val trie: MapPatriciaTrie<Int> = MapPatriciaTrie()

    override suspend fun build(seed: Sequence<String>, progressListener: Channel<Int>?) {
        var count = 0
        seed.forEach {
            trie.insert(it, 0)
            count += it.size + ESTIMATED_LINEBREAK_SIZE /*the line ending*/
            progressListener?.send(count)
        }
        progressListener?.close()
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