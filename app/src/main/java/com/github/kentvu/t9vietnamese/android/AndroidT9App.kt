package com.github.kentvu.t9vietnamese.android

import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.github.kentvu.t9vietnamese.Backend
import com.github.kentvu.t9vietnamese.ui.AppUI
import com.github.kentvu.t9vietnamese.ui.T9App
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import kotlinx.coroutines.Dispatchers

class AndroidT9App(private val mainActivity: MainActivity) :
    T9App(mainActivity.lifecycleScope, Dispatchers.IO) {
    override val ui: AppUI = AndroidUI { mainActivity.finish() }
    override val backend: Backend = Backend(
            ui,
            VietnameseWordList,
            AndroidFileSystem(mainActivity)
        )


    override fun composeClosure(block: @Composable () -> Unit) {
        mainActivity.setContent {
            block()
        }
    }

    fun onKeyEvent(event: KeyEvent): Boolean {
        return ui.onKeyEvent(androidx.compose.ui.input.key.KeyEvent(event))
    }

}
