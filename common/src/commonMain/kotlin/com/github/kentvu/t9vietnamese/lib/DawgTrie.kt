package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Trie
import kotlinx.coroutines.flow.toList
import okio.Buffer
import okio.FileSystem
import okio.Path.Companion.toPath
import org.chalup.dawg.Dawg

class DawgTrie(
    private val fileSystem: FileSystem
) : Trie {
    //val modifiableDAWGSet = ModifiableDAWGSet()
    lateinit var dawg: Dawg

    override suspend fun load(ba: ByteArray) {
        //val dawgSavePath = "${ba.name}.dawg".toPath()
        //modifiableDAWGSet.addAll(source.iterable())
        dawg = Dawg.decode(Buffer().write(ba))
    }

    override fun prefixSearch(prefix: String): Set<String> =
        dawg.prefixSearch(prefix).toSet()

    override fun containsPrefix(prefix: String): Boolean = dawg.containsPrefix(prefix)

}
