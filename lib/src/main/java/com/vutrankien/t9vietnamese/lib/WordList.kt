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

import java.io.Closeable
import java.io.InputStream

interface WordList: Closeable {
    fun forEachPercent(operation: (percentage: Int, Set<String>) -> Any)
}

/**
 * Created by KentVu on 2018/01/21.
 */
class DefaultWordList(val stream: InputStream) :
    WordList {

    private val bufferedReader = stream.bufferedReader()

    override fun close() {
        bufferedReader.close()
    }

    private fun nextWord(): String = bufferedReader.readLine()

    // https://stackoverflow.com/a/6992255
    private fun bytesCount(): Int = stream.available()

    fun nextGroup(numEachTransaction: Int) =
            (0..numEachTransaction).map { nextWord()}

//    fun forEachGroup(groupSize: Int, operation: (progress:Int, Set<String>) -> Any) {
    fun forEachGroup(groupSize: Int, operation: (Set<String>) -> Any) {
        bufferedReader.useLines {
            var count = 0
            it.groupBy {
                count++
                count / groupSize
            }.forEach { operation(it.value.toSet()) }
//            var sequence = it.take(groupSize)
//            do {
//                val group = sequence.toList()
//                group.toSet().let(operation)
//                sequence = sequence.take(groupSize)
//            } while (group.size == groupSize)
        }
//        nextWord().let {  }
//        return operation((0..groupSize).map { nextWord()})
    }

    override fun forEachPercent(operation: (percentage:Int, Set<String>) -> Any) {
        val flength = bytesCount()
        bufferedReader.useLines {
            var bytesRead = 0
            it.groupBy {
                val lastPercentage = bytesRead * 100 / flength
                bytesRead += it.toByteArray().size
                val percentage = bytesRead * 100 / flength
                if (lastPercentage != percentage)
                    println("$percentage% read, bytesRead:$bytesRead / " +
                        "$flength")
                percentage
            }.forEach { operation(it.key, it.value.toSet()) }
            //            do {
//                val group = it.take(groupSize).toList()
//                group.toSet().let(operation)
//            } while (group.size == groupSize)
        }
//        nextWord().let {  }
//        return operation((0..groupSize).map { nextWord()})
    }
}

