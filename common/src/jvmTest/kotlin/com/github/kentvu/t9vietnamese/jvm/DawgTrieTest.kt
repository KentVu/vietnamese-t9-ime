package com.github.kentvu.t9vietnamese.jvm

import com.github.kentvu.t9vietnamese.lib.DawgTrie
import com.github.kentvu.t9vietnamese.model.DecomposedVietnameseWords
import com.github.kentvu.t9vietnamese.model.Trie
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import okio.FileSystem
import okio.source
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res
import java.io.File
import java.net.URI
import kotlin.test.Test

internal class DawgTrieTest {
    @OptIn(ExperimentalResourceApi::class)
    @Test
    fun trieTest() = runTest {
        val trie: Trie =
            DawgTrie(
                DecomposedVietnameseWords(
                    flow { emit(Result.success(File(URI(Res.getUri("files/vi-DauMoi.dic"))).source())) }
                        .catch { emit(Result.failure(it)) }),
                FileSystem.SYSTEM
            )
        trie.load()
        val search = trie.prefixSearch("chà")
        search.forEach { println(it) }
    }
}