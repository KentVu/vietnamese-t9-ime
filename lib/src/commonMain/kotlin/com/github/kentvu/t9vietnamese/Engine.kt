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
    private val fullSequence = StringBuilder(10)

    fun type(keySequence: String) {
        keySequence.forEach { k ->
            val key = keyboard.findKey(k)
            //if(key.isWordTerminal)
            fullSequence.append(k)
            if (fullSequence.length == 1) {
                // Only start searching from 2nd key to prevent too many candidates
                candidates = setOf(
                    fullSequence.toString(),
                    *key.subChars.toCharArray().map { "$it" }.toTypedArray()
                )
            } else {
                key.subChars.forEach { c ->
                    candidates = trie.prefixSearch("$c")
                }
                candidates = setOf(
                    fullSequence.toString(),
                    *key.subChars.toCharArray().map { "$it" }.toTypedArray()
                )
            }
        }
    }

    fun init() {
        trie.load()
    }

}
