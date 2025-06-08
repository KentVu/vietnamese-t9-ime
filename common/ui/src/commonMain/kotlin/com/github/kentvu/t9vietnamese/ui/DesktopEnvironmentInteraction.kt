package com.github.kentvu.t9vietnamese.ui

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import java.net.URI
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okio.FileSystem
import okio.Source
import okio.source
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res

class DesktopEnvironmentInteraction(
    override val mainDispatcher: CoroutineDispatcher = Dispatchers.Main,
    override val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    override val fileSystem: FileSystem = FileSystem.SYSTEM,
    //override val vnTrieSource: Flow<Result<Source>> = flowSourceFrom("files/vi-DauMoi.dawg")
) : EnvironmentInteraction {

    companion object {
        @OptIn(ExperimentalResourceApi::class)
        private fun flowSourceFrom(resPath: String): Flow<Result<Source>> {
            return flow {
                // Java Jar file: use resource errors: URI is not hierarchical
                // https://stackoverflow.com/q/10144210/1562087
                //emit(Result.success(File(URI(Res.getUri("files/vi-DauMoi.dic"))).source()))
                emit(Result.success(URI(Res.getUri(resPath)).toURL().openStream().source()))
            }.catch { emit(Result.failure(it)) }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readVnTrie(): ByteArray {
      return Res.readBytes("files/vi-DauMoi.dawg")
    }
}
