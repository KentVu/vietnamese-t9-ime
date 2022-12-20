package com.github.kentvu.t9vietnamese.jvm

import app.cash.turbine.test
import com.github.kentvu.t9vietnamese.KeySequence
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.KeysCollection
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.test.runTest
import okio.FileSystem

@OptIn(ExperimentalCoroutinesApi::class)
internal class DawgT9EngineTest {
    //@Test
    fun `1Char`() = runTest {
        val element = KeysCollection.key2
        DawgT9Engine(
            DawgTrie(
                DecomposedVietnameseWords(
                    javaClass.classLoader.getResourceAsStream("vi-DauMoi.dic")!!
                ),
                FileSystem.SYSTEM
            ),
            flowOf(KeySequence(listOf(element))).shareIn(this, SharingStarted.Eagerly),
            this
        ).output.test {
            awaitItem().candidates.containsAll(listOf(element.subChars))
        }
    }
}