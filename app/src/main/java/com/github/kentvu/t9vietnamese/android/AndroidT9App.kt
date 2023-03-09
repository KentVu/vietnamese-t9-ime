package com.github.kentvu.t9vietnamese.android

import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.Dispatchers

class AndroidT9App(private val actInt: ActivityInterface) : T9App(
    actInt.scope,
    Dispatchers.IO,
    VietnameseWordList,
    AndroidFileSystem(actInt.context),
) {

    override fun onCloseRequest() {
        actInt.finish()
    }


}
