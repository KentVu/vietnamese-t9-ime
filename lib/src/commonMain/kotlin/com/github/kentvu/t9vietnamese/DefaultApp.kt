package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.App
import com.github.kentvu.t9vietnamese.model.Keyboard
import com.github.kentvu.t9vietnamese.model.WordList
import okio.FileSystem

class DefaultApp(
    private val keyboard: Keyboard,
    wordlist: WordList,
    fileSystem: FileSystem
): App {
    override var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val engine = Engine(keyboard, wordlist, fileSystem)

    override fun type(c: Char) {
        engine.type("${keyboard.findKey(c).symbol}")
        candidates = engine.candidates
    }

    override fun init() {
        engine.init()
    }

}
