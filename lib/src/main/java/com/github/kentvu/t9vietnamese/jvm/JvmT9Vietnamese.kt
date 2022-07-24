package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.DummyKeyPad
import com.github.kentvu.t9vietnamese.Sequencer
import com.github.kentvu.t9vietnamese.T9Vietnamese
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.KeyPad
import com.github.kentvu.t9vietnamese.model.View

class JvmT9Vietnamese(): T9Vietnamese() {

    private val _keyPad = DummyKeyPad()
    override val keyPad: KeyPad = _keyPad

    private val trie = DawgTrie(
        DecomposedVietnameseWords(
            javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic")!!
        )
    )
    private val _view = DummyView(
        DawgT9Engine(
            trie,
            Sequencer.DefaultSequencer(
                _keyPad.keyEvents
            ).output
        ).output
    )
    override val view: View = _view

    init {
    }

    fun init() {
        trie.load()
    }

    override suspend fun type(vararg cs: Char) {
        _keyPad.simulateTyping(*cs)
    }

}
