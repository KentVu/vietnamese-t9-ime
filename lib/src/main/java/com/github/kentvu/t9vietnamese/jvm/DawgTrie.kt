package com.github.kentvu.t9vietnamese.jvm

import org.quinto.dawg.ModifiableDAWGSet

class DawgTrie(val source: Iterable<String>) : Trie {
    val modifiableDAWGSet = ModifiableDAWGSet(source)
    override fun prefixSearch(prefix: String): Set<String> {
        return modifiableDAWGSet.prefixSet(prefix)
    }

}
