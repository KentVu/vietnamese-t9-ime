package com.vutrankien.t9vietnamese.lib.tests.trie

import com.vutrankien.t9vietnamese.lib.DaggerEnvComponent
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldContainKeys
import kentvu.dawgjava.DawgTrie
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class TrieTest: StringSpec({
    val log = DaggerEnvComponent.builder().build().lg.newLog("TrieTest")
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