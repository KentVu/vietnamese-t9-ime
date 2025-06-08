package com.github.kentvu.t9vietnamese.lib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import okio.Source

interface EnvironmentInteraction {
    val mainDispatcher: CoroutineDispatcher
    val ioDispatcher: CoroutineDispatcher
    val fileSystem: FileSystem
    suspend fun readVnTrie(): ByteArray
    //suspend fun openResourceAsText(): Flow<Sequen>
    //suspend fun readResourceFile(path: String): ByteArray

}
