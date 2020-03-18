package com.vutrankien.t9vietnamese.android

import android.content.Context
import com.vutrankien.t9vietnamese.lib.Env
import java.io.File

/** Should be the [ApplicationContext][Context.getApplicationContext]. */
class AndroidEnv(
    private val context: Context
) : Env {
    // https://stackoverflow.com/q/9713981/1562087
    override fun fileExists(path: String): Boolean =
        //context.getFileStreamPath(path).exists()
        File(path).exists()

    override val workingDir: String
        get() = context.filesDir.absolutePath
}
