package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.App
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.WordList
import okio.FileSystem

class DefaultApp(
    private val keyPad: KeyPad,
    wordlist: WordList,
    fileSystem: FileSystem
): App {
    override var candidates: Set<String> = emptySet()
        get() = field
        private set
    private val engine = Engine(keyPad, wordlist, fileSystem)

    override fun type(c: Char) {
        engine.type(keyPad.findKey(c))
        candidates = engine.candidates
    }

    override fun describe(): String {
        return "KeyPad: " + keyPad.describe()
    }

    override fun init() {
        engine.init()
    }

}
