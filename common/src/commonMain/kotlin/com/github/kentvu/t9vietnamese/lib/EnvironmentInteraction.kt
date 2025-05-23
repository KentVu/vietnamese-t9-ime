package com.github.kentvu.t9vietnamese.lib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import okio.Source

interface EnvironmentInteraction {
    val mainDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val fileSystem: FileSystem
    val vnWordsSource: Flow<Result<Source>>

}
