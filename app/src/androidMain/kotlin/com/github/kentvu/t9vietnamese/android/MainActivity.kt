package com.github.kentvu.t9vietnamese.android

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.ui.ComposeUI
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.android.AndroidEnvironmentInteraction
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {

    private val ui by lazy {
        ComposeUI(
            lifecycleScope,
            { finish() }
        )
    }
    private val app by lazy {
        val stateSource: MutableStateFlow<UI.State> = MutableStateFlow(UI.State { })
        T9App/*.create*/(
            AndroidEnvironmentInteraction(this),
            lifecycleScope,
            ui,
            stateSource,
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
