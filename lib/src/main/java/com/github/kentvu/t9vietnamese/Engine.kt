package com.github.kentvu.t9vietnamese

interface T9Engine {

    val output: Output

    interface Output {
        fun receive()
    }
}
