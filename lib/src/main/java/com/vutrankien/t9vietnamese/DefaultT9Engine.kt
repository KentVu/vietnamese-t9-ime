package com.vutrankien.t9vietnamese

import kotlinx.coroutines.Deferred

class DefaultT9Engine(seeds: String, padConfig: PadConfiguration) : T9Engine {
    override var initialized: Boolean = false

    override fun init(): Deferred<Unit> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startInput(): T9Engine.Input {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}
