/*
 * Vietnamese-t9-ime: T9 input method for Vietnamese.
 * Copyright (C) 2020 Vu Tran Kien.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.vutrankien.t9vietnamese.lib

interface LogFactory {
    interface Log {
        fun d(msg: String)
        fun i(msg: String)
        fun w(msg: String)
        fun v(msg: String)
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
    override fun v(msg: String) {
        println(FORMAT.format("V", tag, msg))
    }

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
