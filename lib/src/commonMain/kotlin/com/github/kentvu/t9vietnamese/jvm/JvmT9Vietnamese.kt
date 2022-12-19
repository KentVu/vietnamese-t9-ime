package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.DummyKeyPad
import com.github.kentvu.t9vietnamese.Sequencer
import com.github.kentvu.t9vietnamese.T9Vietnamese
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.View
import kotlinx.coroutines.CoroutineScope
import okio.FileSystem

class JvmT9Vietnamese(
    scope: CoroutineScope, fileSystem: FileSystem
) : T9Vietnamese() {

    private val _keyPad = DummyKeyPad()
    override val keyPad: KeyPad = _keyPad

    private val trie = DawgTrie(
        com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords(
            javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic")!!
        ),
        fileSystem
    )
    private val _view = DummyView(
        DawgT9Engine(
            trie,
            Sequencer.DefaultSequencer(
                _keyPad.keyEvents,
                scope
            ).output,
            scope
        ).output,
        scope
    )
    override val view: View = _view

    init {
    }

    fun init() {
        trie.load()
    }

    override suspend fun type(cs: String) {
        _keyPad.simulateTyping(cs)
    }

}
