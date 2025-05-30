package com.github.kentvu.t9vietnamese.ui

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import java.io.File
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

object DesktopEnvironmentInteraction : EnvironmentInteraction {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    override val fileSystem: FileSystem
        get() = FileSystem.SYSTEM
    @OptIn(ExperimentalResourceApi::class)
    override val vnWordsSource: Flow<Result<Source>>
        get() = flow {
            //emit(Result.success(File(URI(Res.getUri("files/vi-DauMoi.dic"))).source()))
            emit(Result.success(URI(Res.getUri("files/vi-DauMoi.dic")).toURL().openStream().source()))
        }.catch { emit(Result.failure(it)) }
}
