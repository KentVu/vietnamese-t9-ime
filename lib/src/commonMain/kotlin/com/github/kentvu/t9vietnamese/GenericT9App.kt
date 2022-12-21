package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.model.*
import okio.FileSystem

class GenericT9App(
    override val keyPad: KeyPad,
    wordlist: WordList,
    fileSystem: FileSystem
): T9App {
    override var candidates: Set<String> = emptySet()
        private set
    private val engine = Engine(keyPad, wordlist, fileSystem)

    override fun type(key: Key) {
        engine.type(key)
        candidates = engine.candidates
    }

    override fun describe(): String {
        return "KeyPad: " + keyPad.describe()
    }

    override fun init() {
        engine.init()
    }

}
