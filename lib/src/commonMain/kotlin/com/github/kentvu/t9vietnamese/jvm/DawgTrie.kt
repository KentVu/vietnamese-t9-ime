package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList
import org.chalup.dawg.Dawg

//import org.quinto.dawg.ModifiableDAWGSet

class DawgTrie(val source: WordList) : Trie {
    //val modifiableDAWGSet = ModifiableDAWGSet()
    lateinit var dawg: Dawg
    override fun load() {
        //modifiableDAWGSet.addAll(source.iterable())
        dawg = Dawg.generate(source.toSet().toList())
    }

    override fun prefixSearch(prefix: String): Set<String> =
    //return modifiableDAWGSet.prefixSet(prefix)
        dawg.prefixSearch(prefix).toSet()

}
