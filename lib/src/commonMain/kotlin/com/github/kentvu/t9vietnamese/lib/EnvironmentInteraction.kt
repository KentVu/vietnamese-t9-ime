package com.github.kentvu.t9vietnamese.lib

import kotlinx.coroutines.CoroutineDispatcher
import okio.FileSystem
import okio.Source

interface EnvironmentInteraction {
    val mainDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val fileSystem: FileSystem
    val vnWordsSource: Source

    fun  finish()
}
