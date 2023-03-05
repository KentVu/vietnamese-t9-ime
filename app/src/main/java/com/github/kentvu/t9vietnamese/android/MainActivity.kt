package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.github.kentvu.t9vietnamese.model.VietnameseWordList
import com.github.kentvu.t9vietnamese.ui.T9App
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {
    private val app by lazy {
        T9App(
            lifecycleScope,
            Dispatchers.IO,
            { finish() },
            VietnameseWordList,
            AndroidFileSystem(applicationContext),
            object : T9App.ComposeClosure {
                override fun setContent(block: @Composable () -> Unit) {
                    this@MainActivity.setContent {
                        block()
                    }
                }

            }
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        app.stop()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        Napier.d("$keyCode")
        return event?.let { app.onKeyEvent(androidx.compose.ui.input.key.KeyEvent(it)) }
            ?: super.onKeyUp(keyCode, null)
    }

    @Preview
    @Composable
    fun AppPreview() {
        app.ui.AppUi()
    }
}
