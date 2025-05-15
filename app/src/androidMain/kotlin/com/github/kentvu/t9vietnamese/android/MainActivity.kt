package com.github.kentvu.t9vietnamese.android

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.ui.AndroidPresenter

class MainActivity : ComponentActivity() {

    private val ui by lazy {
        AndroidPresenter(
            lifecycleScope,
            close = { finish() },
            launchSystemImSettings = {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            },
            launchImePicker = {
                getSystemService(InputMethodManager::class.java)
                    .showInputMethodPicker()
            },
        )
    }
    private val app by lazy {
        T9App(
            AndroidEnvironmentInteraction(this),
            lifecycleScope,
            ui,
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NapierLogger.init()
        app.start()
        setContent {
            ui.AppUi()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        app.stop()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        android.util.Log.d("MainActivity", "$keyCode")
        Logger.tag("MainActivity").debug("$keyCode")
        return event?.let { ui.onKeyEvent(androidx.compose.ui.input.key.KeyEvent(it)) }
            ?: super.onKeyUp(keyCode, null)
    }
}
