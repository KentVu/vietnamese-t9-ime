package com.github.kentvu.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import android.text.InputType
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.lifecycleScope
import com.github.kentvu.lib.logging.Logger
import com.github.kentvu.lib.logging.NapierLogger
import com.github.kentvu.t9vietnamese.lib.InputSystemConnection
import com.github.kentvu.t9vietnamese.T9App
import com.github.kentvu.t9vietnamese.model.EditorInfo
import com.github.kentvu.t9vietnamese.ui.ImeServiceUI
import com.stackoverflow.android.KeyboardViewLifecycleOwner
import android.view.inputmethod.EditorInfo as AEditorInfo

class T9Vietnamese : InputMethodService() {
    private lateinit var inputView: ComposeView
    private lateinit var candidatesView: ComposeView
    private val keyboardViewLifecycleOwner = KeyboardViewLifecycleOwner()
    private val scope = keyboardViewLifecycleOwner.lifecycleScope
    private val ui by lazy {
        ImeServiceUI(
            scope,
            inputConnection = object : InputSystemConnection {
                override fun commitText(text: String) {
                    currentInputConnection.commitText(text, 0)
                }

                override fun deleteSurroundingText(beforeLength: Int, afterLength: Int) {
                    currentInputConnection.deleteSurroundingText(beforeLength, afterLength)
                }

                override fun performEditorAction() {
                    currentInputConnection.performEditorAction(currentInputEditorInfo.actionId)
                }
            },
        )
    }
    private val app by lazy {
        T9App(
            AndroidEnvironmentInteraction(this),
            scope,
            ui,
        )
    }

    override fun onCreate() {
        android.util.Log.d("T9VietnameseIME", "onCreate:")
        super.onCreate()
        NapierLogger.init()
        log.debug("onCreateInputView:")
        inputView = ComposeView(this).apply {
            setContent {
                ui.ImeUI()
            }
        }
        candidatesView = ComposeView(this).apply {
            setContent {
                ui.CandidateView()
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

    override fun onStartInputView(info: AEditorInfo?, restarting: Boolean) {
        keyboardViewLifecycleOwner.onResume()
        log.debug("onStartInputView:EditorInfo=type=${info?.inputType?.toString(16)}")
        if (info == null) return
        ui.onStartInputView(EditorInfo.fromNative(info))
      }

    override fun onFinishInputView(finishingInput: Boolean) {
        keyboardViewLifecycleOwner.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        keyboardViewLifecycleOwner.onDestroy()
    }

    companion object {
        private fun EditorInfo.Companion.fromNative(info: AEditorInfo): EditorInfo {
            return EditorInfo(when(info.inputType and InputType.TYPE_MASK_CLASS){
                InputType.TYPE_CLASS_NUMBER,
                InputType.TYPE_CLASS_DATETIME,
                InputType.TYPE_CLASS_PHONE -> EditorInfo.Class.Number
                else ->  EditorInfo.Class.Normal
            })
        }
        private val log = Logger.tag("T9VietnameseIME")
    }
}

