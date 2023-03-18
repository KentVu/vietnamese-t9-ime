package com.github.kentvu.t9vietnamese.lib

import com.github.kentvu.t9vietnamese.model.Trie
import com.github.kentvu.t9vietnamese.model.WordList
import okio.FileSystem
import okio.Path.Companion.toPath
import org.chalup.dawg.Dawg

class DawgTrie(
    private val source: WordList,
    private val fileSystem: FileSystem
) : Trie {
    //val modifiableDAWGSet = ModifiableDAWGSet()
    lateinit var dawg: Dawg
    private val dawgSavePath = "${source.name}.dawg".toPath()

    override fun load() {
        //modifiableDAWGSet.addAll(source.iterable())
        dawg = if (fileSystem.exists(dawgSavePath)) {
            Dawg.decode(fileSystem.source(dawgSavePath))
        } else {
            Dawg.generate(source.toSet().toList()).also {
                it.encode(fileSystem.sink(dawgSavePath))
            }
        }
    }

    override fun prefixSearch(prefix: String): Set<String> =
    //return modifiableDAWGSet.prefixSet(prefix)
        dawg.prefixSearch(prefix).toSet()

    override fun containsPrefix(prefix: String): Boolean = dawg.containsPrefix(prefix)

}