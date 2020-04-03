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

package com.vutrankien.t9vietnamese.lib.tests.trie

import com.vutrankien.t9vietnamese.lib.DaggerEngineComponents
import com.vutrankien.t9vietnamese.lib.LogFactory
import io.kotlintest.matchers.maps.shouldContainKeys
import io.kotlintest.specs.StringSpec
import kentvu.dawgjava.DawgTrie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TrieTest: StringSpec ({
    val log = DaggerEngineComponents.builder().build().lg.newLog("TrieTest")
    "Load large file" {
        // https://discuss.gradle.org/t/how-to-read-a-properties-file/15956/4
        //log.d(System.getProperty("word_list_file"))
        val sortedWords = sortedSetOf<String>() // use String's "natural" ordering
        var count = 0
        var size = 0
        TrieTest::class.java.classLoader.getResourceAsStream("vi-DauMoi.dic").bufferedReader().useLines {
            it.forEach { line ->
                log.v(line)
                sortedWords.add(line)
                size += line.toByteArray().size + 1
            }
        }
        val progresses = Channel<Int>()
        launch { // setup receive head first
            //var countBytes = 0
            var markPos = 0
            progresses.consumeEach { progress ->
                //countBytes+=progress
                count++
                if (count - markPos == REPORT_PROGRESS_INTERVAL) {
                    //print("progress $count/${sortedWords.size}: ${countBytes.toFloat() / size * 100}%\n")
                    print("progress $count/${sortedWords.size}: ${progress.toFloat() / size * 100}%\r")
                    markPos = count
                }
            }
        }
        val dawg = DawgTrie.build("vi-DauMoi.dawg", sortedWords.asSequence(), progresses)
        dawg.run {
            search("an").let {
                it.shouldContainKeys(
                    "an",
                    "ang",
                    "anh"
                )
                log.d("search for an:$it")
            }
        }
    }
}) {
    companion object {
        private const val REPORT_PROGRESS_INTERVAL = 10
    }
}