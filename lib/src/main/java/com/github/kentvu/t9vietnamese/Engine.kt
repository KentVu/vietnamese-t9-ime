package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.jvm.DawgTrie
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList

class Engine(keyboard: Keyboard, private val wordlist: WordList) {
    var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val trie: Trie = DawgTrie(wordlist)

    fun type(s: String) {
        candidates = trie.prefixSearch("s")
    }

    fun init() {
        trie.load()
    }

}
