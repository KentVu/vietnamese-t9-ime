package com.github.kentvu.t9vietnamese.android

import androidx.lifecycle.lifecycleScope
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.Dispatchers

class AndroidT9App(private val mainActivity: MainActivity) : T9App(
    mainActivity.lifecycleScope,
    Dispatchers.IO,
    VietnameseWordList,
    AndroidFileSystem(mainActivity),
) {

    override fun onCloseRequest() {
        mainActivity.finish()
    }


}
