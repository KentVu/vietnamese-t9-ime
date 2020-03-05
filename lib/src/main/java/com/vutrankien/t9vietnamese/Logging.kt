package com.vutrankien.t9vietnamese

import javax.inject.Inject

/**
 * Created by vutrankien on 17/07/21.
 */
class LogGenerator @Inject constructor() {
    fun newLog(tag: String): Log = JavaLog(tag)

    interface Log {
        fun d(msg: String)
        fun i(msg: String)
        fun w(msg: String)
    }
}

private class JavaLog(private val tag: String) : LogGenerator.Log {
    override fun d(msg: String) {
        println(FORMAT.format("D", tag, msg))
    }

    override fun i(msg: String) {
        println(FORMAT.format("I", tag, msg))
    }

    override fun w(msg: String) {
        println(FORMAT.format("W", tag, msg))
    }

    companion object {
        private const val FORMAT = "%s [%s]:%s"
    }
}
