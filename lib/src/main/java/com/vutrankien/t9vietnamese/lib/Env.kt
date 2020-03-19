package com.vutrankien.t9vietnamese.lib

/** Environment adaptation layer */
interface Env {
    fun fileExists(path: String): Boolean
    val workingDir: String
}
