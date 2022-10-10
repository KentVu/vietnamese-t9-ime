package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.WordList

class App(private val keyboard: Keyboard, private val wordlist: WordList) {
    var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val engine = Engine(keyboard, wordlist)

    fun type(c: Char) {
        engine.type("${keyboard.findKey(c).symbol}")
        candidates = engine.candidates
    }

    fun init() {
        engine.init()
    }

}
