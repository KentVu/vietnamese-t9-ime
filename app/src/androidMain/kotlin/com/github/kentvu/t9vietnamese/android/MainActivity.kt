package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.ui.AppUI

class MainActivity : ComponentActivity() {
    private val app by lazy {
        AndroidActivityT9App(
            this
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NapierLogger.init()
        app.start()
        setContent {
            AppUI.Ui(app.ui)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        app.stop()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        android.util.Log.d("MainActivity", "$keyCode")
        Logger.tag("MainActivity").debug("$keyCode")
        return event?.let { app.onKeyEvent(androidx.compose.ui.input.key.KeyEvent(it)) }
            ?: super.onKeyUp(keyCode, null)
    }
}
