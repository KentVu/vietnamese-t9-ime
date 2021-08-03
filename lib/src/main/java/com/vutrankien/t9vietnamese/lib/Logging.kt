package com.vutrankien.t9vietnamese.lib

interface LogFactory {
    interface Log {
        fun d(msg: String)
        fun i(msg: String)
        fun w(msg: String)
        fun v(msg: String)
        fun e(msg: String)
    }

    fun newLog(tag: String): Log
}
/**
 * Created by vutrankien on 17/07/21.
 */
object JavaLogFactory: LogFactory {
    override fun newLog(tag: String): LogFactory.Log =
        JavaLog(tag)
}

private class JavaLog(private val tag: String) :
    LogFactory.Log {

    override fun v(msg: String) {
        format("V", msg)
    }

    override fun d(msg: String) {
        format("D", msg)
    }

    override fun i(msg: String) {
        format("I", msg)
    }

    override fun w(msg: String) {
        format("W", msg)
    }

    override fun e(msg: String) {
        format("E", msg)
    }

    private fun format(lvl: String, msg: String) {
        println(FORMAT.format(Thread.currentThread().name, lvl, tag, msg))
    }

    companion object {
        private const val FORMAT = "[%s]%s [%s]:%s"
    }
}
