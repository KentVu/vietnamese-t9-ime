package com.vutrankien.t9vietnamese.lib

interface Seed {
    fun sequence(): Sequence<String>

    object EmptySeed : Seed {
        override fun sequence(): Sequence<String> {
            return emptySequence()
        }
    }

}
