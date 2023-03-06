package com.github.kentvu.t9vietnamese.desktop

import androidx.compose.ui.window.ApplicationScope
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okio.FileSystem

class DesktopT9App(private val applicationScope: ApplicationScope) : T9App(
    CoroutineScope(Dispatchers.Main),
    Dispatchers.IO,
    VietnameseWordList,
    FileSystem.SYSTEM
) {

    override fun onCloseRequest() {
        applicationScope.exitApplication()
    }

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
