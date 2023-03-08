package com.github.kentvu.t9vietnamese.android

import android.inputmethodservice.InputMethodService
import androidx.compose.ui.platform.ComposeView
import com.github.kentvu.t9vietnamese.ui.AppUI

class T9Vietnamese : InputMethodService() {
    private lateinit var inputView: ComposeView
    lateinit var candidatesView: ComposeView
    val ui : AppUI()

    override fun onCreate() {
        super.onCreate()
        inputView = ComposeView(this).apply {
            setContent {

            }
        }
            candidatesView = (layoutInflater.inflate(R.layout.candidates_view, null) as RecyclerView)
    }
}
