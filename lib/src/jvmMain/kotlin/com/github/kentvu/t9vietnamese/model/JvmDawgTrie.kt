package com.github.kentvu.t9vietnamese.model

import com.github.kentvu.t9vietnamese.jvm.DawgTrie
import okio.FileSystem

class JvmDawgTrie(source: WordList) : Trie by DawgTrie(source, FileSystem.SYSTEM) {
}