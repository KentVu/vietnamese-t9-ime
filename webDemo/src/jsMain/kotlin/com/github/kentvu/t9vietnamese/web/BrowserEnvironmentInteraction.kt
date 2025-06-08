package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import okio.FileSystem
import okio.Source
import okio.fakefilesystem.FakeFileSystem
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res

object BrowserEnvironmentInteraction: EnvironmentInteraction {
  override val mainDispatcher: CoroutineDispatcher
    = Dispatchers.Main
  override val ioDispatcher: CoroutineDispatcher
    = Dispatchers.Default
  override val fileSystem: FileSystem
    = FakeFileSystem()

  @OptIn(ExperimentalResourceApi::class)
  override suspend fun readVnTrie(): ByteArray {
    return Res.readBytes("files/vi-DauMoi.dawg")
  }

}
