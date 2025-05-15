package com.github.kentvu.t9vietnamese.web

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Source
import okio.fakefilesystem.FakeFileSystem

class BrowserEnvironmentInteraction: EnvironmentInteraction {
  override val mainDispatcher: CoroutineDispatcher
    = Dispatchers.Main
  override val ioDispatcher: CoroutineDispatcher
    = Dispatchers.Default
  override val fileSystem: FileSystem
    = FakeFileSystem()
  override val vnWordsSource: Source
    = TODO("Not yet implemented")
}