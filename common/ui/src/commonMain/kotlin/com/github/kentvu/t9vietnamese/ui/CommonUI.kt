package com.github.kentvu.t9vietnamese.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEvent
import com.github.kentvu.t9vietnamese.Presenter
import com.github.kentvu.t9vietnamese.Presenter.State

interface CommonUI {

    @Composable
    fun ImeUI(state: State, modifier: Modifier = Modifier)
    @Composable
    fun CandidateView(state: State)
    /** Notify key events to upper layers. */
    fun onKeyEvent(keyEvent: KeyEvent): Boolean
}
