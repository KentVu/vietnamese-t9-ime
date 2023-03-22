package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.ui.window.ApplicationScope
import com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okio.FileSystem
import okio.Source
import okio.source

class DesktopT9App(private val applicationScope: ApplicationScope) : T9App(
    object : EnvironmentInteraction {
        override val mainDispatcher: CoroutineDispatcher
            get() = Dispatchers.Main
        override val ioDispatcher: CoroutineDispatcher
            get() = Dispatchers.IO
        override val fileSystem: FileSystem
            get() = FileSystem.SYSTEM
        override val vnWordsSource: Source
            get() = DesktopT9App::class.java.classLoader?.getResourceAsStream("vi-DauMoi.dic")!!.source()

        override fun finish() {
            applicationScope.exitApplication()
        }
    },
) {

    //@Composable
    //fun startForTest() {
    //    //Napier.base(DebugAntilog("T9Desktop"))
    //    LaunchedEffect(1) {
    //        backend.init()
    //    }
    //    ui.buildUi()
    //}

    //fun ensureBackendInitialized() {
    //    backend.ensureInitialized()
    //}
}
