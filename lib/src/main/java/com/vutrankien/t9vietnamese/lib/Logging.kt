package com.vutrankien.t9vietnamese.lib

interface LogFactory {
    interface Log {
        fun d(msg: String)
        fun i(msg: String)
        fun w(msg: String)
    }

    fun newLog(tag: String): Log
}
/**
 * Created by vutrankien on 17/07/21.
 */
class JavaLogFactory: LogFactory {
    override fun newLog(tag: String): LogFactory.Log =
        JavaLog(tag)
}

private class JavaLog(private val tag: String) :
    LogFactory.Log {
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
