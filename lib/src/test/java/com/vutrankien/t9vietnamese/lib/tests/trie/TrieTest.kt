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
                //log.d(line)
                sortedWords.add(line)
                size += line.toByteArray().size + 1
            }
        }
        DawgTrie("vi-DauMoi.dawg").run {
            val progresses = Channel<Int>()
            launch {
                build(sortedWords.asSequence(), progresses)
            }
            var countBytes = 0
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