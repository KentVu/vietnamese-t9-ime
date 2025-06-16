package com.github.kentvu.t9vietnamese.android

import android.content.Context
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import okio.FileSystem
import okio.Source
import okio.source
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res
import kotlin.text.removePrefix

class AndroidEnvironmentInteraction(private val context: Context) : EnvironmentInteraction {
    override val mainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val ioDispatcher: CoroutineDispatcher
        get() = Dispatchers.IO
    override val fileSystem: FileSystem
        get() = AndroidFileSystem(context)

    @OptIn(ExperimentalResourceApi::class)
    override suspend fun readVnTrie(): ByteArray {
      return Res.readBytes("files/vi-DauMoi.dawg")
    }
}
