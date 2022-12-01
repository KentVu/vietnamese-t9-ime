package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList
//import org.quinto.dawg.ModifiableDAWGSet

class DawgTrie(val source: WordList) : Trie {
//    val modifiableDAWGSet = ModifiableDAWGSet()
    override fun load() {
//        modifiableDAWGSet.addAll(source.iterable())
        TODO("Use dawg-kotlin.")
    }

    override fun prefixSearch(prefix: String): Set<String> {
//        return modifiableDAWGSet.prefixSet(prefix)
        TODO("Use dawg-kotlin.")
    }

}
