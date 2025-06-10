package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.Presenter.State
import com.github.kentvu.t9vietnamese.UI
import com.github.kentvu.t9vietnamese.model.EditorInfo

interface CommonUI: UI {

    @Composable
    fun ImeUI(state: State, modifier: Modifier = Modifier)
    @Composable
    fun CandidateView(state: State)
    /** Notify key events to domain layers. */
    fun onKeyEvent(keyEvent: KeyEvent): Boolean
}
