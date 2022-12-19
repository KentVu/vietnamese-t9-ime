package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.jvm.DawgTrie
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList
import okio.FileSystem

class Engine(
    private val keyboard: Keyboard,
    wordlist: WordList,
    fileSystem: FileSystem
) {
    var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val trie: Trie = DawgTrie(wordlist, fileSystem)
    private val fullSequence = StringBuilder(10)
    private var prefixes: Set<String> = emptySet()

    fun type(keySequence: String) {
        keySequence.forEach { k ->
            val key = keyboard.findKey(k)
            //if(key.isWordTerminal)
            fullSequence.append(k)
            val _candidates = mutableSetOf(fullSequence.toString())
            val _prefixes = mutableSetOf<String>()
            if (fullSequence.length == 1) {
                // Only start searching from 2nd key to prevent too many candidates
                //_candidates.addAll(key.subChars.map { "$it" })
                //prefixes = it.toSet() // assuming trie has 1 character prefix
                key.subChars.map { "$it" }.forEach { c ->
                    if (trie.containsPrefix(c)) {
                        _candidates.add(c)
                        _prefixes.add(c)
                    }
                }
            } else {
                prefixes.forEach { pf ->
                    key.subChars.forEach { sc ->
                        if (trie.containsPrefix(pf + sc)) {
                            _prefixes.add(pf + sc)
                            _candidates.addAll(trie.prefixSearch(pf + sc))
                        }
                    }
                }
            }
            prefixes = _prefixes
            candidates = _candidates
        }
    }

    fun init() {
        trie.load()
    }

}
