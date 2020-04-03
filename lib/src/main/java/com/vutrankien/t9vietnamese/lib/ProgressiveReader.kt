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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import java.io.InputStream

// TODO: Stop estimating and add a parameter (May Android won't change the line ending convention :) )
const val ESTIMATED_LINEBREAK_SIZE = 1 // assume linux/mac line endings. (\r, \n only)

@ExperimentalCoroutinesApi
fun InputStream.progressiveRead(scope: CoroutineScope = GlobalScope): ReceiveChannel<Progress> {
    return scope.produce {
        val bufferedReader = this@progressiveRead.bufferedReader()
        bufferedReader.useLines {
            var bytesRead = 0
            it.forEach { line ->
                bytesRead += line.toByteArray().size + ESTIMATED_LINEBREAK_SIZE
                send(Progress(bytesRead, line))
            }
        }
    }
}

data class Progress(val bytesRead: Int, val line: String)
