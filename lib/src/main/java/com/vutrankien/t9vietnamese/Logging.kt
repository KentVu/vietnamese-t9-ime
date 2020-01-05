package com.vutrankien.t9vietnamese

/**
 * Created by vutrankien on 17/07/21.
 */
interface Logging {
    fun d(msg: String)
    fun i(msg: String)
    fun w(msg: String)
}


class JavaLog(private val tag: String) : Logging {
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
