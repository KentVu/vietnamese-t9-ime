package com.vutrankien.t9vietnamese

import com.vutrankien.t9vietnamese.trie.Trie
import kotlinx.coroutines.*

class DefaultT9Engine(
    trie: Trie,
    override val pad: PadConfiguration
) : T9Engine {
    override var initialized: Boolean = false

    override suspend fun init() {
        initialized = true
        //TODO()
        delay(10)
    }

    override fun startInput(): T9Engine.Input {
        return Input()
    }

    class Input : T9Engine.Input {
        override val confirmed: Boolean
            get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

        override fun push(key: Key) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun result(): List<String> {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

}
