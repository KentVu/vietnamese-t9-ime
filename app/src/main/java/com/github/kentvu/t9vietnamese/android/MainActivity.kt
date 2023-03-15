package com.github.kentvu.t9vietnamese.android

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope

class MainActivity : ComponentActivity() {
    private val app by lazy {
        AndroidActivityT9App(
            this
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app.start()
        setContent {
            app.ui.AppUi()
        }
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
