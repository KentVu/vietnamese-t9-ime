package com.vutrankien.t9vietnamese.android

import android.content.Context
import android.util.AttributeSet
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.appcompat.widget.AppCompatEditText

/**
 * Exposing [inputConnection] for testing.
 */
class MyEditText(context: Context?, attrs: AttributeSet?) : AppCompatEditText(context, attrs) {
    private val log = AndroidLogFactory.newLog("MyEditText")
    internal var inputConnection: InputConnection? = null

    override fun onCreateInputConnection(outAttrs: EditorInfo?): InputConnection {
        log.d("onCreateInputConnection:$outAttrs")
        inputConnection = super.onCreateInputConnection(outAttrs)
        return inputConnection!!
    }
}