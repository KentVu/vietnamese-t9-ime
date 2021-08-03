package com.vutrankien.t9vietnamese.lib.tests

import com.vutrankien.t9vietnamese.lib.Env
import com.vutrankien.t9vietnamese.lib.JvmEnv

internal object NoFileEnv: Env by JvmEnv {
    override fun fileExists(path: String): Boolean = false
}
