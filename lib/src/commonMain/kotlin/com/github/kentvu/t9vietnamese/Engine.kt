package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.jvm.DawgTrie
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList

class Engine(private val keyboard: Keyboard, private val wordlist: WordList) {
    var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val trie: Trie = DawgTrie(wordlist)

    fun type(keySequence: String) {
        keySequence.forEach { k ->
            val key = keyboard.findKey(k)
            key.subChars.forEach { c ->
                candidates = trie.prefixSearch("$c")
            }
        }
    }

    fun init() {
        trie.load()
    }

}
