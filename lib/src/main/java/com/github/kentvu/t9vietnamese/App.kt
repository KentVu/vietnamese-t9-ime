package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.WordList

class App(private val keyboard: Keyboard, private val wordlist: WordList) {
    private val _candidates = mutableSetOf<String>()
    val candidates: Set<String> = _candidates

    fun type(c: Char) {
        Engine(keyboard, wordlist).type("${keyboard.findKey(c)}")
    }

    fun verifyCandidatesContainOnly(s: String) {

    }

}
