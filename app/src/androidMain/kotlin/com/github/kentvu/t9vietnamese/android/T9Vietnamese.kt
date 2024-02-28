package com.github.kentvu.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.compose.ui.platform.ComposeView
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.ui.T9App
import com.stackoverflow.android.KeyboardViewLifecycleOwner

class T9Vietnamese : InputMethodService() {
    companion object {
        private val log = Logger.tag("T9VietnameseIME")
    }
    private lateinit var inputView: ComposeView
    private lateinit var candidatesView: ComposeView
    private val app by lazy {
        object : T9App(
            env = object : AndroidEnvironmentInteraction(this){
                // stopSelf?
                override fun finish() = Unit
            }
        ){
            override val ui: ImeServiceUI = ImeServiceUI(scope, this)
        }
    }

    private val keyboardViewLifecycleOwner = KeyboardViewLifecycleOwner()
    override fun onCreate() {
        android.util.Log.d("T9VietnameseIME", "onCreate:")
        super.onCreate()
        NapierLogger.init()
        log.debug("onCreateInputView:")
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
        log.debug("onCreateInputView:")
        //Compose uses the decor view to locate the "owner" instances
        keyboardViewLifecycleOwner.attachToDecorView(
            window?.window?.decorView
        )

        return inputView
    }

    override fun onCreateCandidatesView(): View {
        log.debug("onCreateCandidatesView:")
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

}
