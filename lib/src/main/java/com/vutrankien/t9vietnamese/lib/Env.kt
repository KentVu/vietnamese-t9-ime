package com.vutrankien.t9vietnamese.lib

interface Env {
    fun fileExists(path: String): Boolean
    val workingDir: String
}
