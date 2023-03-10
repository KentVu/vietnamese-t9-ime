package com.github.kentvu.t9vietnamese.android

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import com.stackoverflow.android.KeyboardViewLifecycleOwner
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class T9Vietnamese : InputMethodService(),
    com.github.kentvu.t9vietnamese.lib.EnvironmentInteraction {
    private lateinit var inputView: ComposeView
    private lateinit var candidatesView: ComposeView
    private val app by lazy { AndroidT9App(this) }

    private val keyboardViewLifecycleOwner = KeyboardViewLifecycleOwner()
    override fun onCreate() {
        super.onCreate()
        Napier.base(DebugAntilog())
        inputView = ComposeView(this).apply {
            setContent {
                app.ui.ImeUI()
            }
        }
        candidatesView = ComposeView(this).apply {
            setContent {
                app.ui.CandidatesView()
            }
        }
        app.start()
        keyboardViewLifecycleOwner.onCreate()
    }

    override fun onCreateInputView(): View {
        //return super.onCreateInputView()
        Napier.d(":")
        //Compose uses the decor view to locate the "owner" instances
        keyboardViewLifecycleOwner.attachToDecorView(
            window?.window?.decorView
        )

        return inputView
    }

    override fun onCreateCandidatesView(): View {
        Napier.d("onCreateCandidatesView:")
        setCandidatesViewShown(true)
        return candidatesView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        keyboardViewLifecycleOwner.onResume()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        keyboardViewLifecycleOwner.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardViewLifecycleOwner.onDestroy()
    }

    override val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
    override val context: Context
        get() = this

    override fun finish() {
        // Don't exit in service
    }
}
