package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.KeySequence
import com.github.kentvu.t9vietnamese.T9Engine
import com.github.kentvu.t9vietnamese.model.Trie
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform

class DawgT9Engine(val trie: Trie, input: Flow<KeySequence>) : T9Engine(input) {
    override val output: Flow<T9EngineOutput> =
        input.transform { keySequence: KeySequence ->
            //for (i in 2..keySequence.length)
            //keySequence.first(i)
        }

}
