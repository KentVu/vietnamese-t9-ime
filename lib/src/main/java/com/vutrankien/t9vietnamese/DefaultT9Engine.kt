package com.vutrankien.t9vietnamese

import kotlinx.coroutines.*

class DefaultT9Engine(
        seeds: String,
        override val pad: PadConfiguration
) : T9Engine {
    override var initialized: Boolean = false

    override fun init(scope: CoroutineScope): Deferred<Unit> {
        initialized = true
        return scope.async {
            //TODO()
            delay(1000)
        }
    }

    override fun startInput(): T9Engine.Input {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
