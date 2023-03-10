package com.github.kentvu.t9vietnamese.android

import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import kotlinx.coroutines.Dispatchers

class AndroidT9App(private val env: com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction) : T9App(
    env.scope,
    Dispatchers.IO,
    VietnameseWordList,
    AndroidFileSystem(env.context),
) {

    override fun onCloseRequest() {
        env.finish()
    }


}
