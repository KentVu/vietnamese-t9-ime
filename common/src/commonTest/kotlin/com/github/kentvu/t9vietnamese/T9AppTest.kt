package com.github.kentvu.t9vietnamese

import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.tests.TestPresenter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import okio.Buffer
import okio.ByteString.Companion.encodeUtf8
import okio.ByteString.Companion.toByteString
import okio.FileSystem
import okio.Source
import okio.fakefilesystem.FakeFileSystem
import org.jetbrains.compose.resources.ExperimentalResourceApi
import t9vietnamese.common.generated.resources.Res
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class T9AppTest {
    val scope = CoroutineScope(Dispatchers.Default)
    val presenter = TestPresenter()
    val app: T9App = setupApp()

    fun setupApp(): T9App {
        return T9App(
            FakeEnvironmentInteraction(),
            scope,
            presenter
        )
    }

    @BeforeTest
    fun setup() {
        //app = setupApp()
    }

    @Test
    fun displayInitializedWhenTrieLoaded() = runTest {
        app.start()
        assertTrue(presenter.stateHistory.last().initialized)
    }

    class FakeEnvironmentInteraction : EnvironmentInteraction {
        override val mainDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.Default
        override val fileSystem: FileSystem = FakeFileSystem()

        @OptIn(ExperimentalResourceApi::class)
        override suspend fun readVnTrie(): ByteArray {
            return Res.readBytes("files/vi-DauMoi.dawg")
        }
    }
}
