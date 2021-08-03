package com.vutrankien.t9vietnamese.lib

import java.io.File

/** Environment adaptation layer */
interface Env {
    fun fileExists(path: String): Boolean
    val workingDir: String
}

object JvmEnv : Env {
    override fun fileExists(path: String): Boolean =
        File(path).exists()

    override val workingDir: String
        get() = "."
}
