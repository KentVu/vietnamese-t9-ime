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

package com.vutrankien.t9vietnamese.console

import com.vutrankien.t9vietnamese.lib.VietnameseWordSeed.decomposeVietnamese
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {
    val fromFile = File(args[0])
    val wd = Paths.get("").toAbsolutePath()
    val toFile = File(fromFile.parent, "${fromFile.name}.sorted")
    println("Sorting file $fromFile wd=$toFile")
    val sorted = sortedSetOf<String>()
    fromFile.bufferedReader().useLines { lines ->
        lines.forEach { sorted.add(it.decomposeVietnamese()) }
    }
    toFile.bufferedWriter().use { writer -> sorted.forEach { writer.write(it + "\n") } }
    print("Written to $toFile")
}

class MyClass {
}