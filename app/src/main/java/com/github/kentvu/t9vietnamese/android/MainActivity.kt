package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import io.github.aakira.napier.Napier

class MainActivity : ComponentActivity() {
    private val app = AndroidT9App(this)

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
        return event?.let { app.onKeyEvent(it) }
            ?: super.onKeyUp(keyCode, null)
    }
}
